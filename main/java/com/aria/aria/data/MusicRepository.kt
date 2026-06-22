package com.aria.aria.data

import android.content.Context
import android.provider.MediaStore
import com.aria.aria.data.model.Song

object MusicRepository {

    fun loadDeviceSongs(context: Context): List<Song> {

        val songs = mutableListOf<Song>()

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION
        )


        val selection = "${MediaStore.Audio.Media.IS_MUSIC}=1"

        val cursor = context.contentResolver.query(
            uri,
            projection,
            selection,
            null,
            null
        )

        cursor?.use {

            val idIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val albumIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)

            while (it.moveToNext()) {
                val id = it.getLong(idIndex)
                val title = it.getString(titleIndex)
                val artist = it.getString(artistIndex)
                val duration = it.getLong(durationIndex)
                val album = it.getString(albumIndex)

                val contentUri = android.content.ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                songs.add(
                    Song(
                        id = id,
                        title = title ?: "Unknown",
                        uri = contentUri.toString(),
                        artist = artist,
                        duration = duration,
                        album = album
                    )
                )
            }

        }

        return songs
    }
}