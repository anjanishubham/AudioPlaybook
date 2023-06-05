package com.example.audioplayer.data.repository

import com.example.audioplayer.data.ContentResolverHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AudioRepository @Inject constructor(private val contentResolverHelper: ContentResolverHelper) {
    suspend fun getAudioList() = withContext(Dispatchers.IO){
        contentResolverHelper.getAudioData()
    }
}