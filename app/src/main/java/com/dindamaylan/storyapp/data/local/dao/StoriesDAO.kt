package com.dindamaylan.storyapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dindamaylan.storyapp.data.local.Stories

@Dao
interface StoriesDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStories(stories: List<Stories>)

    @Query("select * from stories")
    fun getStories(): PagingSource<Int, Stories>

    @Query("delete from stories")
    suspend fun deleteStories()
}