package com.dindamaylan.storyapp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dindamaylan.storyapp.R
import com.dindamaylan.storyapp.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : androidx.fragment.app.Fragment() {
    private var isBinding: FragmentLoginBinding? = null
    private val binding get() = isBinding!!

    private val loginViewModel: LoginViewModel by activityViewModels()
    private var jobLogin: Job = Job()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isBinding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doActions()
    }

    private fun doActions() {
        binding.apply {
            btnLogin.setOnClickListener {
                doLogin()
            }
            ctaRegist.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
    }

    private fun doLogin() {
        val email = binding.cetEmail.text.toString()
        val password = binding.cetPassword.text.toString()
        loadingState(true)

        lifecycleScope.launchWhenResumed {
            if (jobLogin.isActive) jobLogin.cancel()

            jobLogin = launch {
                binding.tvAlert.visibility = View.INVISIBLE
                loginViewModel.login(email, password).collect {
                    it.onSuccess { login ->
                        login.loginResult?.token?.let { token ->
                            loginViewModel.authToken(token)
                        }
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.message_login_200),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    it.onFailure {
                        binding.tvAlert.visibility = View.VISIBLE

                        loadingState(false)
                    }
                }
            }
        }

    }

    private fun loadingState(isLoad: Boolean) {
        binding.apply {
            cetEmail.isEnabled = !isLoad
            cetPassword.isEnabled = !isLoad
            btnLogin.isEnabled = !isLoad

            if (isLoad) {
                pbLoading.visibility = View.VISIBLE
            } else {
                pbLoading.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isBinding = null
    }


}