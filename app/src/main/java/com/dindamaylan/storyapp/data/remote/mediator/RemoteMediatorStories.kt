package com.dindamaylan.storyapp.data.remote.mediator

import android.annotation.SuppressLint
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.LoadType.*
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.dindamaylan.storyapp.data.local.RemotePager
import com.dindamaylan.storyapp.data.local.Stories
import com.dindamaylan.storyapp.data.local.dao.DbStories
import com.dindamaylan.storyapp.data.remote.retrofit.ApiService
import com.dindamaylan.storyapp.data.repository.AuthUser
import kotlinx.coroutines.flow.first

@ExperimentalPagingApi
class RemoteMediatorStories(
    private val apiService: ApiService,
    private val dbStories: DbStories,
    private val authUser: AuthUser
) : RemoteMediator<Int, Stories>() {

    private companion object {
        const val ID_PAGER = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    @SuppressLint("SuspiciousIndentation")
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Stories>
    ): MediatorResult {
        val pager = when (loadType) {
            REFRESH -> {
                ID_PAGER
            }
            PREPEND -> {
                val index = getRemotePagerFirstly(state)
                index?.previousPager
                    ?: return MediatorResult.Success(endOfPaginationReached = index != null)
            }
            APPEND -> {
                val index = getRemotePagerFinalLast(state)
                index?.nextPager
                    ?: return MediatorResult.Success(endOfPaginationReached = index != null)
            }
        }

        try {
            val token = "Bearer ${authUser.getTokenAuth().first()}"
            val response = apiService.getStories(token, pager, state.config.pageSize)
            val endOfPaginationReached = response.listStory.isEmpty()

            val previousPager = if (pager == 1) null else pager - 1
            val nextPager = if (endOfPaginationReached) null else pager + 1
            val currentPager = response.listStory.map {
                RemotePager(id = it.id, previousPager = previousPager, nextPager = nextPager)
            }

            if (loadType == REFRESH) {
                dbStories.remoteKeysMediaStoriesDAO().deleteRemotePager()
                dbStories.storiesDAO().deleteStories()
            }

            dbStories.remoteKeysMediaStoriesDAO().addRemotePager(currentPager)

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
            Log.d("TAG", "load: ${response.listStory}")
            dbStories.storiesDAO().addStories(stories = stories)
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemotePagerFirstly(state: PagingState<Int, Stories>): RemotePager? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            dbStories.remoteKeysMediaStoriesDAO().getRemotePager(data.id)
        }
    }

    private suspend fun getRemotePagerFinalLast(state: PagingState<Int, Stories>): RemotePager? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            dbStories.remoteKeysMediaStoriesDAO().getRemotePager(data.id)
        }
    }

}