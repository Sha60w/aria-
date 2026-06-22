package com.aria.aria.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.SkipNext
import com.aria.aria.player.AriaPlayerManager
import com.aria.aria.ui.theme.AriaGold
import com.aria.aria.ui.components.SongArtwork


@Composable
fun MiniPlayer(
    player: AriaPlayerManager,
    onOpenNowPlaying: () -> Unit
) {
    if (player.currentTitle.value.isBlank()) return

    val glowAlpha = animateFloatAsState(
        targetValue = if (player.isPlaying.value) 0.45f else 0.18f,
        label = "miniPlayerGlow"
    ).value

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onOpenNowPlaying() },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.60f)
        ),
        border = BorderStroke(1.dp, AriaGold.copy(alpha = glowAlpha))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = player.currentTitle.value,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = if (player.isPlaying.value) "Playing" else "Paused",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (player.isPlaying.value) AriaGold else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(onClick = { player.previous() }) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = MaterialTheme.colorScheme.onSurface)
                }

                IconButton(
                    onClick = {
                        if (player.isPlaying.value) player.pause() else player.resume()
                    },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = if (player.isPlaying.value) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = AriaGold
                    )
                }

                IconButton(onClick = { player.next() }) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

