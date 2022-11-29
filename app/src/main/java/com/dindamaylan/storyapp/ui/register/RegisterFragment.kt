package com.dindamaylan.storyapp.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dindamaylan.storyapp.R
import com.dindamaylan.storyapp.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var isBinding: FragmentRegisterBinding? = null
    private val binding get() = isBinding!!

    private val registerViewModel: RegisterViewModel by viewModels()
    private var jobRegister: Job = Job()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isBinding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doActions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isBinding = null
    }

    private fun doActions() {
        binding.apply {
            btnRegister.setOnClickListener {
                doRegister()
            }
            ctaLogin.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
    }

    private fun doRegister() {
        val name = binding.cetName.text.toString().trim()
        val email = binding.cetEmail.text.toString().trim()
        val password = binding.cetPassword.text.toString()
        loadingState(true)

        lifecycleScope.launchWhenResumed {
            if (jobRegister.isActive) jobRegister.cancel()

            jobRegister = launch {
                registerViewModel.register(name, email, password).collect {
                    it.onSuccess {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.message_register_200),
                            Toast.LENGTH_SHORT
                        ).show()

                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    }

                    it.onFailure {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.message_register_400),
                            Toast.LENGTH_SHORT
                        ).show()

                        loadingState(false)
                    }
                }
            }
        }
    }

    private fun loadingState(isLoad: Boolean) {
        binding.apply {
            cetName.isEnabled = !isLoad
            cetEmail.isEnabled = !isLoad
            cetPassword.isEnabled = !isLoad
            btnRegister.isEnabled = !isLoad

            if (isLoad) {
                pbLoading.visibility = View.VISIBLE
            } else {
                pbLoading.visibility = View.GONE
            }
        }
    }
}