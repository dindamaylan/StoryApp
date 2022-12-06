package com.dindamaylan.storyapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dindamaylan.storyapp.data.local.Stories
import com.dindamaylan.storyapp.data.local.dao.DbStories
import com.dindamaylan.storyapp.data.remote.mediator.RemoteMediatorStories
import com.dindamaylan.storyapp.data.remote.retrofit.ApiService
import com.dindamaylan.storyapp.data.remote.retrofit.response.ResponsePostStory
import com.dindamaylan.storyapp.utils.Helper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@ExperimentalPagingApi
class RepoStories @Inject constructor(
    private val apiService: ApiService,
    private val dbStories: DbStories,
    private val authUser: AuthUser
    ) {

    fun getStories(): Flow<PagingData<Stories>> {
        return Pager(
            config = PagingConfig(pageSize = 2),
            remoteMediator = RemoteMediatorStories (
                apiService,
                dbStories,
                authUser
        ),
        pagingSourceFactory = { dbStories.storiesDAO().getStories() }, initialKey = null
        ).flow.flowOn(Dispatchers.IO)
    }

    fun getStoriesbyLocation(): Flow<Result<List<Stories>>> = flow{
        try {
            val token = BEARER.plus(authUser.getTokenAuth().first())

            val response = apiService.getStories(token, location = 1)
            val stories = response.listStory.map {
                Stories(
                    it.id,
                    it.name ?: "",
                    it.description ?: "",
                    it.photoUrl ?: "",
                    it.createdAt ?: "",
                    it.lon,
                    it.lat
                )
            }
            emit(Result.success(stories))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

    suspend fun postStories(
        file: File,
        description: String,
        lat: Double?,
        lon: Double?
    ): Flow<Result<ResponsePostStory>> = flow {
        try {
            val token = BEARER.plus(authUser.getTokenAuth().first())

            val desc = description.toRequestBody("text/plain".toMediaType())
            val img = Helper.reduceFileImage(file)
            val imgMultiPart: MultipartBody.Part =
                MultipartBody.Part.createFormData(
                    "photo",
                    img.name,
                    img.asRequestBody("image/jpg".toMediaTypeOrNull())
                )

            var latitude : RequestBody? = null
            var longitude : RequestBody? = null

            if (lat != null && lon != null) {
                latitude = lat.toString().toRequestBody("text/plain".toMediaType())
                longitude = lon.toString().toRequestBody("text/plain".toMediaType())
            }

            val response = apiService.postStories(token, imgMultiPart, desc, latitude, longitude)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

    companion object{
        private const val BEARER = "Bearer "
    }
}