package com.dindamaylan.storyapp.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dindamaylan.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthUser @Inject constructor(
    private val apiService: ApiService,
    private val dataStore: DataStore<Preferences>
)  {

    companion object{
        private val TOKEN = stringPreferencesKey("token")
    }

    suspend fun login(email:String, password:String) = flow {
        try {
            val apiResponse = apiService.postLogin(email, password)
            Log.d("TAG", "doLogin: $apiResponse")
            emit(Result.success(apiResponse))
        }catch (e:Exception){
            e.printStackTrace()
            Log.d("TAG", "doLogin: ${e.message}")
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    fun getTokenAuth(): kotlinx.coroutines.flow.Flow<String> {
        return dataStore.data.map { value: Preferences ->
            value[TOKEN]?:""
        }
    }

    suspend fun saveAuthToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN] = token
        }
    }

    suspend fun register(name:String, email:String, password:String) = flow {
        try {
            val apiResponse = apiService.postRegister(name, email, password)
            emit(Result.success(apiResponse))
        }catch (e:Exception){
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}
