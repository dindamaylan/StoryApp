package com.dindamaylan.storyapp.ui.register

import androidx.lifecycle.ViewModel
import com.dindamaylan.storyapp.data.remote.retrofit.response.ResponseRegister
import com.dindamaylan.storyapp.data.repository.AuthUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val authUser: AuthUser): ViewModel(){
    suspend fun register(name:String, email: String, password: String): Flow<Result<ResponseRegister>> = authUser.register(name, email, password)
}