package com.dindamaylan.storyapp.ui.story.add

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.ExperimentalPagingApi
import com.dindamaylan.storyapp.data.remote.retrofit.response.ResponsePostStory
import com.dindamaylan.storyapp.data.repository.RepoStories
import com.dindamaylan.storyapp.utils.CoroutinesRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalPagingApi::class)
class StoryAddViewModelTest{
    @get:Rule
    var coroutinesRule = CoroutinesRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repoStories: RepoStories
    private lateinit var storyAddViewModel: StoryAddViewModel

    @Before
    fun setUp(){
        storyAddViewModel = StoryAddViewModel(repoStories)
    }

    @Test
    fun `when uploadStory should not null and result success`() = runTest {
        val dummyFile = File("path")
        val dummyDesc = "description"
        val expected = true

        val data = flow{emit(Result.success(ResponsePostStory(false, "success")))}

        `when` (repoStories.postStories(dummyFile, dummyDesc, null, null)).thenReturn(data)

        storyAddViewModel.uploadStory(dummyFile, dummyDesc, null, null)
        var actual: Boolean? = null

        storyAddViewModel.stateUpload.observeForever{
            actual = it
        }

        Mockito.verify(repoStories).postStories(dummyFile, dummyDesc, null, null)
        Assert.assertNotNull(actual)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `when uploadStory should not null and result failure`() = runTest {
        val dummyFile = File("path")
        val dummyDesc = "description"
        val expected = false

        val data: Flow<Result<ResponsePostStory>> = flow {
            emit(Result.failure(Exception()))
        }

        `when` (repoStories.postStories(dummyFile, dummyDesc, null, null)).thenReturn(data)

        storyAddViewModel.uploadStory(dummyFile, dummyDesc, null, null)
        var actual: Boolean? = null

        storyAddViewModel.stateUpload.observeForever{
            actual = it
        }

        Mockito.verify(repoStories).postStories(dummyFile, dummyDesc, null, null)
        Assert.assertNotNull(actual)
        Assert.assertEquals(expected, actual)
    }
}