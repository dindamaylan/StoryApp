package com.dindamaylan.storyapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_pager")
data class RemotePager(
    @PrimaryKey
    val id: String,
    val previousPager: Int?,
    val nextPager: Int?
)