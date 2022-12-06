package com.dindamaylan.storyapp.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dindamaylan.storyapp.data.local.Stories
import com.dindamaylan.storyapp.data.repository.RepoStories
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class DashboardViewModel @Inject constructor(private val repoStories: RepoStories) : ViewModel() {
    fun getStories(): LiveData<PagingData<Stories>> =
        repoStories.getStories().cachedIn(viewModelScope).asLiveData()
}