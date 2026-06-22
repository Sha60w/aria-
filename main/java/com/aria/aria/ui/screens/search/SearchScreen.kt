package com.aria.aria.ui.screens.search

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aria.aria.data.MusicRepository
import com.aria.aria.data.model.Song
import com.aria.aria.ui.components.PremiumButton
import com.aria.aria.ui.components.PremiumSurface
import com.aria.aria.ui.theme.AriaGold
import com.aria.aria.ui.components.SongArtwork


@Composable
fun SearchScreen(
    context: Context,
    onPlay: (Song) -> Unit
) {
    val allSongs = remember {
        MusicRepository.loadDeviceSongs(context)
    }

    var query by remember { mutableStateOf("") }

    val filteredSongs = remember(query, allSongs) {
        val q = query.trim()
        if (q.isEmpty()) allSongs.take(120) else
            allSongs.filter {
                it.title.contains(q, ignoreCase = true) ||
                    (it.artist ?: "").contains(q, ignoreCase = true) ||
                    (it.album ?: "").contains(q, ignoreCase = true)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PremiumSurface(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Search", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(10.dp))

                TextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search songs, artists...") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                PremiumButton(
                    text = "Clear",
                    enabled = query.isNotBlank(),
                    onClick = { query = "" }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            items(filteredSongs, key = { it.id }) { song ->
                PremiumSurface(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPlay(song) }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
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
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    song.artist ?: "Unknown Artist",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text("Play", style = MaterialTheme.typography.labelLarge, color = AriaGold)
                    }

                }
            }
        }
    }
}

