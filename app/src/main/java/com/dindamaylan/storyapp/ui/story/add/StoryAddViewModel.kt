package com.dindamaylan.storyapp.ui.story.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import com.dindamaylan.storyapp.data.repository.RepoStories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class StoryAddViewModel @Inject constructor(
    private val repoStories: RepoStories
) : ViewModel() {

    private val _stateUpload: MutableLiveData<Boolean> = MutableLiveData()
    val stateUpload: LiveData<Boolean> = _stateUpload

    fun uploadStory(
        file: File,
        description: String,
        lat: Double? = null,
        lon: Double? = null
    ) {
        viewModelScope.launch {
            repoStories.postStories(file, description, lat, lon).collect { result ->
                result.onSuccess {
                    _stateUpload.value = true
                }
                result.onFailure {
                    _stateUpload.value = false
                }
            }
        }
    }
}