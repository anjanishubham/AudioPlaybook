package com.example.audioplayer.data

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.WorkerThread
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ContentResolverHelper @Inject constructor(@ApplicationContext val context:Context) {
    private var mCursor: Cursor? = null

    private val projection:Array<String> = arrayOf(
        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
        MediaStore.Audio.AudioColumns._ID,
        MediaStore.Audio.AudioColumns.ARTIST,
        MediaStore.Audio.AudioColumns.DATA,
        MediaStore.Audio.AudioColumns.DURATION,
        MediaStore.Audio.AudioColumns.TITLE
    )

    private var selectionClause:String? =
        "${MediaStore.Audio.AudioColumns.IS_MUSIC} = ?"

    private var selectionArg = arrayOf("1")

    private val sortOrder = "${MediaStore.Audio.AudioColumns.DISPLAY_NAME} ASC"

    @WorkerThread
    fun getAudioData():List<Audio>{
        return getCursorData()
    }

    private fun getCursorData():MutableList<Audio>{
        var audioList = ArrayList<Audio>()
        mCursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selectionClause,
            selectionArg,
            sortOrder)

        mCursor?.use { mCursor ->
            val idColumn =
                mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
            val displayNameColumn =
                mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
            val displayArtistColumn =
                mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
            val dataColumn =
                mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
            val titleColumn =
                mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
            val durationColumn = mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)

            mCursor.apply {
                if(count == 0){
                    Log.d("Cursor", "getCursorData: cursor is empty ")
                }else{
                    while (this.moveToNext()){
                        val displayName = getString(displayNameColumn)
                        val id = getLong(idColumn)
                        val artist = getString(displayArtistColumn)
                        val data = getString(dataColumn)
                        val duration = getInt(durationColumn)
                        val title = getString(titleColumn)
                        val uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id)
                        audioList+=Audio(
                            uri =uri,
                            displayName = displayName,
                            id = id,
                            artist = artist,
                            duration = duration,
                            title = title,
                            data = data)
                    }
                }

            }

        }
        return audioList






    }
}