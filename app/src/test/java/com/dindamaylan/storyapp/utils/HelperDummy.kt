package com.dindamaylan.storyapp.utils

import com.dindamaylan.storyapp.data.local.Stories
import com.dindamaylan.storyapp.data.remote.retrofit.response.LoginResult
import com.dindamaylan.storyapp.data.remote.retrofit.response.ResponseLogin
import com.dindamaylan.storyapp.data.remote.retrofit.response.ResponseRegister

object HelperDummy {
    fun generateUserDummy(): ResponseLogin{
        val loginResult = LoginResult(
            name = "Dinda",
            userId = "user-DUw9UnpFhTwYkfWj",
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLURVdzlVbnBGaFR3WWtmV2oiLCJpYXQiOjE2Njk4NTUxMzd9.E4GBZPEcjuPtNGZ6kzFyiWxJfk7wB92kNvE-l8lPBtQ"
        )

        return ResponseLogin(
            loginResult = loginResult,
            error = false,
            message = "success"
        )
    }

    fun createUserDummy(): ResponseRegister{
        return ResponseRegister(
            error = false,
            message = "success"
        )
    }

    fun generateDummyStories(): List<Stories>{
        val items: MutableList<Stories> = arrayListOf()

        for (i in 0..30){
            val stories = Stories(
                "id_$i",
                name = "lala_$i",
                description = "desc_$i",
                photoUrl = "photo_$i",
                createdAt = "created_$i",
                lon = 0.0,
                lat = 0.0
            )
            items.add(stories)
        }
        return items
    }
}

