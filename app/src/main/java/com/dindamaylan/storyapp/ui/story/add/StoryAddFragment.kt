package com.dindamaylan.storyapp.ui.story.add

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.ExperimentalPagingApi
import com.dindamaylan.storyapp.R
import com.dindamaylan.storyapp.databinding.FragmentStoryAddBinding
import com.dindamaylan.storyapp.utils.Helper
import com.dindamaylan.storyapp.utils.Helper.animVisibility
import com.dindamaylan.storyapp.utils.Helper.bitmapToFile
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
@ExperimentalPagingApi
class StoryAddFragment : Fragment() {
    private var isBinding: FragmentStoryAddBinding? = null
    private val binding get() = isBinding!!

    private val storyAddViewModel: StoryAddViewModel by viewModels()
    private val arg: StoryAddFragmentArgs by navArgs()
    private var fileImg: File? = null
    private lateinit var launcherGalery : ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isBinding = FragmentStoryAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launcherGalery =
            registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
                if (result != null) {
                    Helper.uriToFile(result, requireContext()).also { fileImg = it }
                    binding.ivStory.setImageURI(result)
                }
            }

        val photoFile = arg.photo
        if (photoFile != null) {
            val rotate = arg.rotate
            val isFromFolder = arg.isFromFolder

            val photo = if (isFromFolder) {
                BitmapFactory.decodeFile(photoFile.path)
            } else Helper.rotateBitmap(
                BitmapFactory.decodeFile(photoFile.path),
                rotate
            )

            binding.ivStory.setImageBitmap(photo)
            fileImg = photo.bitmapToFile(photoFile)
        }

        checkBeforeUpload()

        with(binding) {
            btnCamera.setOnClickListener { openCamera() }
            btnGalery.setOnClickListener { openGalery() }
            btnUpload.setOnClickListener { postStory() }
        }
    }

    private fun checkBeforeUpload() {
        var isDescExs: Boolean

        binding.cetDesc.doAfterTextChanged {
            val cetDesc = it.toString()
            if (cetDesc.isBlank()) {
                binding.etDesc.error = getString(R.string.alert_upload)
                isDescExs = false
            } else {
                binding.etDesc.error = null

                isDescExs = true
            }
            binding.btnUpload.isEnabled = isDescExs && fileImg != null
        }
    }

    private fun openGalery() {
        launcherGalery.launch("image/*")
    }

    private fun postStory() {
        loadingState(true)

        val des = binding.cetDesc.text.toString()

        storyAddViewModel.uploadStory(fileImg!!, des)
        storyAddViewModel.stateUpload.observe(viewLifecycleOwner) { state ->
            if (state) {
                loadingState(false)
                goToDashboard()
                Toast.makeText(
                    context,
                    getString(R.string.message_upload_201),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                loadingState(false)
                Toast.makeText(
                    context,
                    getString(R.string.message_upload_400),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun openCamera() {
        findNavController().navigate(R.id.action_storyAddFragment_to_cameraXFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isBinding = null
    }

    private fun loadingState(isLoading: Boolean) {
        binding.apply {
            btnUpload.isEnabled = !isLoading
            btnGalery.isEnabled = !isLoading
            btnCamera.isEnabled = !isLoading

            viewLoading.animVisibility(isLoading)
        }
    }

    private fun goToDashboard() {
        val navOptions =
            NavOptions.Builder().setLaunchSingleTop(true).setPopUpTo(R.id.navigation_main, true)
                .build()
        findNavController().navigate(R.id.dashboardFragment, null, navOptions)
    }
}