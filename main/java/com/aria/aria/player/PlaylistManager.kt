package com.aria.aria.player

import com.aria.aria.data.model.Song
import com.aria.aria.model.Playlist
import androidx.compose.runtime.mutableStateListOf

object PlaylistManager {

    val playlists = mutableStateListOf<Playlist>()

    fun createPlaylist(name: String) {
        playlists.add(
            Playlist(name, emptyList())
        )
    }

    fun addSongToPlaylist(playlistName: String, song: Song) {
        val index = playlists.indexOfFirst { it.name == playlistName }

        if (index != -1) {
            val updated = playlists[index].copy(
                songs = playlists[index].songs + song
            )

            playlists[index] = updated
        }
    }
}