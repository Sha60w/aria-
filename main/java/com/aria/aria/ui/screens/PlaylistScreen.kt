package com.aria.aria.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aria.aria.data.model.Song
import com.aria.aria.player.PlaylistManager
import com.aria.aria.ui.components.PremiumButton
import com.aria.aria.ui.components.PremiumSurface
import com.aria.aria.ui.components.SongArtwork


@Composable
fun PlaylistScreen(
    onOpenPlaylist: (String) -> Unit
) {

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        PremiumSurface(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Playlists", style = MaterialTheme.typography.headlineSmall)
                PremiumButton(
                    text = "Create Playlist",
                    onClick = {
                        PlaylistManager.createPlaylist("My Playlist ${PlaylistManager.playlists.size + 1}")
                    }
                )
            }
        }

        PremiumSurface(modifier = Modifier.fillMaxWidth()) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 6.dp)
            ) {
                items(PlaylistManager.playlists) { playlist ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .clickable { onOpenPlaylist(playlist.name) },
                        tonalElevation = 2.dp,
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(playlist.name, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "${playlist.songs.size} songs",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text("Open", color = com.aria.aria.ui.theme.AriaGold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlaylistDetailScreen(
    playlistName: String,
    onPlayQueue: (List<Song>, Int) -> Unit
) {

    val playlist = PlaylistManager.playlists.find { it.name == playlistName }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        PremiumSurface(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(playlistName, style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    if (playlist == null) "Not found" else "${playlist.songs.size} songs",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        val songs = playlist?.songs.orEmpty()
        if (songs.isEmpty()) {
            PremiumSurface(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth().padding(18.dp), contentAlignment = Alignment.Center) {
                    Text("Empty playlist", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                itemsIndexed(songs) { index, song ->
                    PremiumSurface(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp, vertical = 6.dp)
                        .clickable { onPlayQueue(songs, index) }) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SongArtwork(
                                    artworkUri = song.artworkUri,
                                    modifier = Modifier.size(44.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(song.title, style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(song.artist, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Text("Play", color = com.aria.aria.ui.theme.AriaGold)
                        }

                    }
                }
            }
        }
    }
}

