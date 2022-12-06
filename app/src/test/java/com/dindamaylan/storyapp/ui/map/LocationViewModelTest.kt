package com.dindamaylan.storyapp.ui.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.ExperimentalPagingApi
import com.dindamaylan.storyapp.data.local.Stories
import com.dindamaylan.storyapp.data.repository.RepoStories
import com.dindamaylan.storyapp.utils.CoroutinesRule
import com.dindamaylan.storyapp.utils.HelperDummy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalPagingApi::class)
class LocationViewModelTest{
    @get:Rule
    var coroutinesRule = CoroutinesRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repoStories: RepoStories
    private lateinit var locationViewModel: LocationViewModel

    @Before
    fun setup(){
        locationViewModel = LocationViewModel(repoStories)
    }

    @Test
    fun `when get stories by location should not null and result success`() = runTest {
        val expect = flow{ emit(Result.success(HelperDummy.generateDummyStories())) }

        `when` (repoStories.getStoriesbyLocation()).thenReturn(expect)

        var actual: Result<List<Stories>> ?= null
        locationViewModel.getStoriesByLocation().observeForever{
            actual = it
        }

        Mockito.verify(repoStories).getStoriesbyLocation()
        Assert.assertNotNull(actual)
        Assert.assertEquals(expect.first(), actual)
    }

    @Test
    fun `when get stories by location should not null and result failure`() = runTest {
        val expect: Flow<Result<List<Stories>>> = flowOf( Result.failure(Exception()))

        `when` (repoStories.getStoriesbyLocation()).thenReturn(expect)

        var actual: Result<List<Stories>> ?= null
        locationViewModel.getStoriesByLocation().observeForever{
            actual = it
        }

        Mockito.verify(repoStories).getStoriesbyLocation()
        Assert.assertNotNull(actual)
        println(expect.first())
        println(actual)
        Assert.assertEquals(expect.first(), actual)
    }
}