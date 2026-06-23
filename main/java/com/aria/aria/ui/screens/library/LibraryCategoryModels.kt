package com.aria.aria.ui.screens.library

import com.aria.aria.data.model.Song

data class ArtistBucket(
    val artist: String,
    val songs: List<Song>,
)

data class AlbumBucket(
    val artist: String,
    val album: String,
    val songs: List<Song>,
    val artworkUri: String?,
)

data class FolderBucket(
    val folder: String,
    val songs: List<Song>,
)

