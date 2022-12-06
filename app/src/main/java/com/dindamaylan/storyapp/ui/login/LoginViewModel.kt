package com.dindamaylan.storyapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dindamaylan.storyapp.data.remote.retrofit.response.ResponseLogin
import com.dindamaylan.storyapp.data.repository.AuthUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authUser: AuthUser): ViewModel(){

    suspend fun login(email: String, password: String): Flow<Result<ResponseLogin>> = authUser.login(email, password)

    fun authToken(token: String){
        viewModelScope.launch {
            authUser.saveAuthToken(token)
        }
    }

    val getAuthToken: LiveData<String> by lazy {authUser.getTokenAuth().asLiveData()}
}
