package com.dindamaylan.storyapp.ui.others

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dindamaylan.storyapp.R
import com.dindamaylan.storyapp.databinding.FragmentOthersBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OthersFragment : Fragment() {
    private var isBinding: FragmentOthersBinding? = null
    private val binding get() = isBinding!!

    private val othersViewModel: OthersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isBinding = FragmentOthersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            ctaLogout.setOnClickListener {
                showLogoutConfirmation()
            }
        }
    }

    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.logout_confirmation_title))
            .setPositiveButton(getString(R.string.confirmation_positive)){ _, _ ->
                othersViewModel.authToken("")
                Toast.makeText(
                    requireContext(),
                    getString(R.string.message_logout_200),
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton(getString(R.string.confirmation_negative)){ dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}