package com.dindamaylan.storyapp.data.remote.retrofit

import com.dindamaylan.storyapp.data.remote.retrofit.response.ResponseGetStories
import com.dindamaylan.storyapp.data.remote.retrofit.response.ResponseLogin
import com.dindamaylan.storyapp.data.remote.retrofit.response.ResponsePostStory
import com.dindamaylan.storyapp.data.remote.retrofit.response.ResponseRegister
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    suspend fun postLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): ResponseLogin

    @FormUrlEncoded
    @POST("register")
    suspend fun postRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): ResponseRegister

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null
    ): ResponseGetStories

    @Multipart
    @POST("stories")
    suspend fun postStories(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): ResponsePostStory
}