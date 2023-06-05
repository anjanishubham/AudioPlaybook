package com.example.audioplayer.media.exoplayer

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import com.example.audioplayer.data.repository.AudioRepository
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import javax.inject.Inject

class MediaSource @Inject constructor(private val audioRepository: AudioRepository) {

    private val onReadyListeners: MutableList<OnReadyListener> = mutableListOf()
    private val audioMediaMetaData: List<MediaMetadataCompat> = emptyList()
    private var state: AudioSourceState = AudioSourceState.STATE_CREATED
    set(value) {
        if(value == AudioSourceState.STATE_CREATED ||
                value == AudioSourceState.STATE_ERROR){
            synchronized(onReadyListeners){
                field = value
                onReadyListeners.forEach { listener:OnReadyListener ->
                    listener.invoke(isReady)
                }
            }
        }else{
            field = value
        }
        }

    fun whenReady(listener: OnReadyListener):Boolean{
      return  if(state == AudioSourceState.STATE_CREATED ||
                state == AudioSourceState.STATE_INITIALIZING){
            onReadyListeners+=listener
            false
        }else{
            listener.invoke(isReady)
            true
        }
    }

    suspend fun loadMediaMetadata(){
        state = AudioSourceState.STATE_INITIALIZING
        val data = audioRepository.getAudioList().map { audio ->
            MediaMetadataCompat.Builder().putString(
                MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                audio.id.toString()
            ).putString(
                MediaMetadataCompat.METADATA_KEY_MEDIA_URI,
                audio.uri.toString()
            ).putString(
                MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST,
                audio.artist
            ).putString(
                MediaMetadataCompat.METADATA_KEY_TITLE,
                audio.title
            ).putString(
                MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE,
                audio.displayName
            ).putString(
                MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST,
                audio.artist
            ).putString(
                MediaMetadataCompat.METADATA_KEY_DURATION,
                audio.duration.toString()
            )

        }
        state = AudioSourceState.STATE_INITIALIZED
    }

    fun getMediaSource(dataSource: CacheDataSource.Factory):ConcatenatingMediaSource{
        val concatenatingMediaSource = ConcatenatingMediaSource()
        audioMediaMetaData.forEach { mediaMetadataCompat ->
            val mediaItem = MediaItem.fromUri(
                mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI)
            )
            val mediaSource = ProgressiveMediaSource.Factory(dataSource).createMediaSource(mediaItem)

            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    fun getMediaDescription() = audioMediaMetaData.map { metaData ->
        val description = MediaDescriptionCompat
            .Builder()
            .setMediaId(metaData.description.mediaId)
            .setDescription(metaData.description.description)
            .setMediaUri(metaData.description.mediaUri)
            .setTitle(metaData.description.title)
            .setSubtitle(metaData.description.subtitle)
            .build()
            MediaBrowserCompat.MediaItem(description,FLAG_PLAYABLE)
    }.toMutableList()


    fun refresh(){
        onReadyListeners.clear()
        state = AudioSourceState.STATE_CREATED
    }


    private val isReady: Boolean
        get() = state == AudioSourceState.STATE_INITIALIZED
}




enum class AudioSourceState{
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR,
}


typealias OnReadyListener = (Boolean) -> Unit