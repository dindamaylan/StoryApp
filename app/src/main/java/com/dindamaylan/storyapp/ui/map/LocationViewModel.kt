package com.dindamaylan.storyapp.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.paging.ExperimentalPagingApi
import com.dindamaylan.storyapp.data.local.Stories
import com.dindamaylan.storyapp.data.repository.RepoStories
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalPagingApi::class)

class LocationViewModel
@Inject constructor(
    private val repoStories: RepoStories
) : ViewModel() {

    fun getStoriesByLocation(): LiveData<Result<List<Stories>>> {
        return repoStories.getStoriesbyLocation().asLiveData()
    }
}