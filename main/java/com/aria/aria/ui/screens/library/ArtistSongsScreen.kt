package com.aria.aria.ui.screens.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aria.aria.data.model.Song
import com.aria.aria.player.AriaPlayerManager
import com.aria.aria.ui.components.PremiumSurface
import com.aria.aria.ui.components.SongArtwork
import com.aria.aria.ui.theme.AriaGold

@Composable
fun ArtistSongsScreen(
    player: AriaPlayerManager,
    songs: List<Song>,
    artist: String,
    onBack: () -> Unit
) {
    val artistSongs = remember(songs, artist) {
        songs.filter { (it.artist ?: "").equals(artist, ignoreCase = false) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            TextButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text("← Back")
            }
            Text(
                text = artist,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if (artistSongs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No songs found", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            return@Column
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 90.dp)
        ) {
            items(artistSongs, key = { it.id }) { song ->
                PremiumSurface(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // Avoid fragile queue indexing; just play the tapped song.
player.playSongInSubsetQueue(artistSongs, song, autoplay = true)
                                }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SongArtwork(
                                    artworkUri = song.artworkUri,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(song.title, style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        song.album,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Play",
                            style = MaterialTheme.typography.labelLarge,
                            color = AriaGold,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }
    }
}

