package com.dindamaylan.storyapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dindamaylan.storyapp.data.local.RemotePager

@Dao
interface RemoteKeysMediaStoriesDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRemotePager(remotePager: List<RemotePager>)

    @Query("select * from remote_pager where id = :id")
    suspend fun getRemotePager(id: String): RemotePager?

    @Query("delete from remote_pager")
    suspend fun deleteRemotePager()
}