package com.dindamaylan.storyapp.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dindamaylan.storyapp.MainActivity
import com.dindamaylan.storyapp.R
import com.dindamaylan.storyapp.data.local.Stories
import com.dindamaylan.storyapp.databinding.FragmentLocationBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationFragment : Fragment(), OnMapReadyCallback {

    private var isBinding: FragmentLocationBinding? = null
    private val binding get() = isBinding as FragmentLocationBinding

    private val locationViewModel: LocationViewModel by viewModels()

    private lateinit var ac: MainActivity
    private lateinit var gmaps: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ac = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isBinding = FragmentLocationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val locationFragment =
            childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        locationFragment.getMapAsync(this)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

    }

    override fun onMapReady(googleMap: GoogleMap) {
        gmaps = googleMap
        gmaps.uiSettings.apply {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }

        gmaps.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(-2.3932797, 108.8507139), 5f
            )
        )

        locationViewModel.getStoriesByLocation().observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                setUpMarkerStory(it, gmaps)
            }
            result.onFailure {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.alert_location),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setUpMarkerStory(listStories: List<Stories>, googleMap: GoogleMap) {
        listStories.forEach {
            if (it.lat != null && it.lon != null) {
                val latLon = LatLng(it.lat, it.lon)
                googleMap.addMarker(
                    MarkerOptions()
                        .position(latLon)
                        .title(it.name)
                        .icon(
                            BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)
                        )
                )
            }
        }
    }
}