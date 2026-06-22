package com.aria.aria.model

import com.aria.aria.data.model.Song

data class Playlist(
    val name: String,
    val songs: List<Song>
)