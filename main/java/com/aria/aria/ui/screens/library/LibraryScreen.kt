package com.aria.aria.ui.screens.library

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.core.content.ContextCompat
import com.aria.aria.data.MusicScanner
import com.aria.aria.data.model.Song
import com.aria.aria.player.PlayerHolder
import com.aria.aria.ui.components.PremiumSurface
import com.aria.aria.ui.theme.AriaGold
import com.aria.aria.ui.components.SongArtwork


@Composable
fun LibraryScreen(navController: androidx.navigation.NavController) {
// val navController = androidx.navigation.compose.rememberNavController()
    // IMPORTANT: this screen is hosted inside the root NavHost; do not create a new nested NavController.
    // Navigation must use the parent controller; otherwise route pushes can crash/tear down the UI.


    val context = LocalContext.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    val playerManager = PlayerHolder.getPlayer(context)

    var sortMode by remember { mutableStateOf(SortMode.Songs) }
    var query by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        if (!hasPermission) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Button(onClick = {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
                }) {
                    Text("Grant Audio Permission")
                }
            }
            return@Box
        }

        val songs: List<Song> = remember {
            MusicScanner().scanSongs(context)
        }

        val filtered = remember(songs, query) {
            val q = query.trim()
            if (q.isEmpty()) songs else
                songs.filter {
                    it.title.contains(q, ignoreCase = true) ||
                        it.artist.contains(q, ignoreCase = true) ||
                        it.album.contains(q, ignoreCase = true)
                }
        }

        if (filtered.isEmpty()) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No music found", style = MaterialTheme.typography.titleMedium)
            }
            return@Box
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Top controls
            PremiumSurface(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Library", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(10.dp))

                    TextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Search title, artist, album") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(text = "Songs", selected = sortMode == SortMode.Songs) { sortMode = SortMode.Songs }
                        FilterChip(text = "Artists", selected = sortMode == SortMode.Artists) { sortMode = SortMode.Artists }
                        FilterChip(text = "Albums", selected = sortMode == SortMode.Albums) { sortMode = SortMode.Albums }
                        FilterChip(text = "Folders", selected = sortMode == SortMode.Folders) { sortMode = SortMode.Folders }
                    }

                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Songs tab: keep existing search + sorting behavior.
            val songsOrdered = remember(filtered) {
                filtered.sortedWith(
                    compareBy<Song> { it.title.lowercase() }.thenBy { it.artist.lowercase() }
                )
            }

            // Grouped collections for category browser tabs.
            val artistsMap = remember(filtered) {
                filtered.groupBy { it.artist.trim().ifBlank { "Unknown" } }
            }

            data class AlbumItem(
                val album: String,
                val artist: String,
                val songs: List<Song>
            )

            val albumsMap = remember(filtered) {
                // include artist in the key to avoid album-name collisions across artists
                filtered.groupBy { "${it.album.trim().ifBlank { "Unknown" }}|||${it.artist.trim().ifBlank { "Unknown" }}" }
            }

            val foldersMap = remember(filtered) {
                filtered.groupBy { extractFolderName(it.uri) }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 90.dp)
            ) {
                when (sortMode) {
                    SortMode.Songs -> {
                        items(songsOrdered, key = { it.id }) { song ->
                            PremiumSurface(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    androidx.compose.foundation.layout.Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                playerManager.clearQueue()
                                                playerManager.setQueue(songsOrdered)
                                                val startIndex = songsOrdered.indexOfFirst { it.id == song.id }
                                                if (startIndex >= 0) {
                                                    playerManager.playQueueAt(startIndex, autoplay = true)
                                                } else {
                                                    playerManager.playSong(song.title, song.uri, song.artworkUri)
                                                }
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
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
                                                    song.artist,
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

                    SortMode.Artists -> {
                        val artistNames = artistsMap.keys.sortedBy { it.lowercase() }
                        items(artistNames, key = { it }) { artistName ->
                            val artistSongs = artistsMap[artistName].orEmpty()
                            if (artistSongs.isEmpty()) return@items

                            val artworkUri = artistSongs.firstOrNull()?.artworkUri

                            PremiumSurface(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    androidx.compose.foundation.layout.Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
navController.navigate(
                                                    "artist_songs/${java.net.URLEncoder.encode(artistName, "UTF-8")}" 
                                                )

                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            SongArtwork(artworkUri = artworkUri, modifier = Modifier.size(44.dp))
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(artistName, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    "${artistSongs.size} songs",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    SortMode.Albums -> {
                        val albumItems = albumsMap.entries.map { (key, songs) ->
                            val parts = key.split("|||")
                            val albumName = parts.getOrNull(0)?.trim()?.ifBlank { "Unknown" } ?: "Unknown"
                            val artistName = parts.getOrNull(1)?.trim()?.ifBlank { "Unknown" } ?: "Unknown"
                            AlbumItem(album = albumName, artist = artistName, songs = songs)
                        }.sortedBy { it.album.lowercase() }

                        items(albumItems, key = { it.album + "|||" + it.artist }) { item ->
                            val artworkUri = item.songs.firstOrNull()?.artworkUri

                            PremiumSurface(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    androidx.compose.foundation.layout.Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
navController.navigate(
"album_songs/${java.net.URLEncoder.encode(item.album, "UTF-8")}/${java.net.URLEncoder.encode(item.artist, "UTF-8")}"
                                                )
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            SongArtwork(artworkUri = artworkUri, modifier = Modifier.size(44.dp))
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(item.album, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    "${item.artist} • ${item.songs.size} songs",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    SortMode.Folders -> {
                        val folderNames = foldersMap.keys.sortedBy { it.lowercase() }
                        items(folderNames, key = { it }) { folderName ->
                            val folderSongs = foldersMap[folderName].orEmpty()
                            if (folderSongs.isEmpty()) return@items

                            PremiumSurface(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    androidx.compose.foundation.layout.Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
navController.navigate(
                                                    "folder_songs/${java.net.URLEncoder.encode(folderName ?: "", "UTF-8")}" 
                                                )
                                            }
                                    ) {
                                        Column {
                                            Text(folderName, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                "${folderSongs.size} songs",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


private enum class SortMode {
    Songs,
    Artists,
    Albums,
    Folders
}

private fun extractFolderName(uri: String): String {
    return try {
        val path = android.net.Uri.parse(uri).path ?: return "Unknown"
        val segments = path.split('/').filter { it.isNotBlank() }
        if (segments.isEmpty()) return "Unknown"
        // Use last parent directory as folder name.
        if (segments.size >= 2) segments[segments.size - 2] else "Root"
    } catch (_: Exception) {
        "Unknown"
    }
}

private fun StringKey(s: String?): String {
    return (s ?: "Unknown").trim().ifBlank { "Unknown" }
}

@Composable

private fun FilterChip(

    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = { Text(text) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
            labelColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
        )
    )
}

