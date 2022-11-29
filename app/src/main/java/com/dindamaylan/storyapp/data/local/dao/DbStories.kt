package com.dindamaylan.storyapp.data.local.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dindamaylan.storyapp.data.local.RemotePager
import com.dindamaylan.storyapp.data.local.Stories

@Database(
    entities = [Stories::class, RemotePager::class],
    version = 2,
    exportSchema = false
)
abstract class DbStories: RoomDatabase(){
    abstract fun storiesDAO(): StoriesDAO
    abstract fun remoteKeysMediaStoriesDAO(): RemoteKeysMediaStoriesDAO
}