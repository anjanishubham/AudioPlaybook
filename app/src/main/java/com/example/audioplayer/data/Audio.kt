package com.example.audioplayer.data

import android.net.Uri
import java.net.URI

data class Audio(
   val uri: Uri,
   val displayName: String,
   val id: Long,
   val artist: String,
   val data: String,
   val duration: Int,
   val title: String
)
