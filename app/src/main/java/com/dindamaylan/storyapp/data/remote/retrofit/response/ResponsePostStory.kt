package com.dindamaylan.storyapp.data.remote.retrofit.response

import com.google.gson.annotations.SerializedName

data class ResponsePostStory(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
