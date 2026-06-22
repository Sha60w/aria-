package com.aria.aria.data

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.aria.aria.data.model.Song


class MusicScanner {

    fun scanSongs(context: Context): List<Song> {


        val songs = mutableListOf<Song>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
        )


        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            MediaStore.Audio.Media.TITLE + " ASC"
        )

        cursor?.use {

            val idColumn =
                it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)

            val titleColumn =
                it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)

            val artistColumn =
                it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)

            val albumColumn =
                it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)

            val durationColumn =
                it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            val albumIdColumn =
                it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)


            while (it.moveToNext()) {

                val id = it.getLong(idColumn)

                val contentUri =
                    android.content.ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                val albumId = it.getString(albumIdColumn)

                val artworkUri = if (albumId.isNullOrBlank()) null else {
                    Uri.withAppendedPath(
                        MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                        albumId
                    ).toString()
                }

                songs.add(
                    Song(
                        id = id,
                        title = it.getString(titleColumn),
                        artist = it.getString(artistColumn),
                        album = it.getString(albumColumn),
                        duration = it.getLong(durationColumn),
                        uri = contentUri.toString(),
                        artworkUri = artworkUri
                    )
                )

            }
        }

        return songs
    }
}