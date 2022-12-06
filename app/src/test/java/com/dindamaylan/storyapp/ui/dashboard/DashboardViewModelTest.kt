package com.dindamaylan.storyapp.ui.dashboard

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.*
import androidx.recyclerview.widget.ListUpdateCallback
import com.dindamaylan.storyapp.data.adapter.ListStoriesAdapter
import com.dindamaylan.storyapp.data.local.Stories
import com.dindamaylan.storyapp.data.repository.RepoStories
import com.dindamaylan.storyapp.utils.CoroutinesRule
import com.dindamaylan.storyapp.utils.HelperDummy
import com.dindamaylan.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
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

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalPagingApi::class)
class DashboardViewModelTest {
    @get:Rule
    var coroutinesRule = CoroutinesRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repoStories: RepoStories
    private lateinit var dashboardViewModel: DashboardViewModel

    @Before
    fun setUp(){
        dashboardViewModel = DashboardViewModel(repoStories)
    }

    @Test
    fun `when get stories should not null and return success`() = runTest {
        val dummy = HelperDummy.generateDummyStories()
        val data: PagingData<Stories> = StoriesPagingSource.snapshot(dummy)
        val expected = flow{ emit(data) }

        `when`(repoStories.getStories()).thenReturn(expected)

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoriesAdapter.callback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        val actual: PagingData<Stories> = dashboardViewModel.getStories().getOrAwaitValue()
        differ.submitData(actual)

        Mockito.verify(repoStories).getStories()
        Assert.assertNotNull(actual)
        Assert.assertEquals(dummy, differ.snapshot())
        Assert.assertEquals(dummy.size, differ.snapshot().size)
        Assert.assertEquals(dummy[0].name, differ.snapshot()[0]?.name)
    }
}

class StoriesPagingSource : PagingSource<Int, Flow<List<Stories>>>() {
    companion object {
        fun snapshot(items: List<Stories>): PagingData<Stories> {
            return PagingData.from(items)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, Flow<List<Stories>>>): Int {
        return 0
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Flow<List<Stories>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

private val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}