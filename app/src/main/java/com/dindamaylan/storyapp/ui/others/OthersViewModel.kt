package com.dindamaylan.storyapp.ui.others

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dindamaylan.storyapp.data.repository.AuthUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OthersViewModel @Inject constructor(private val authUser: AuthUser)  : ViewModel(){
    fun authToken(token: String){
        viewModelScope.launch {
            authUser.saveAuthToken(token)
        }
    }
}