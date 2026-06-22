package com.aria.aria.ui.screens

import androidx.annotation.OptIn
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.aria.aria.player.AriaPlayerManager
import androidx.compose.material3.Slider
import androidx.media3.common.util.UnstableApi
import com.aria.aria.ui.theme.AriaGold
import com.aria.aria.ui.theme.ElevatedSurface
import com.aria.aria.ui.components.SongArtwork


@OptIn(UnstableApi::class)
@Composable
fun NowPlayingScreen(
    player: AriaPlayerManager,
    onBack: () -> Unit
) {
    // Namida-like layout: center “vinyl” art + stacked actions with Aria theme.
    // Keeping existing playback wiring (pause/resume/next/previous) intact.

    var selectedTab by remember { mutableStateOf("lyrics") }

    val playPulse = animateFloatAsState(
        targetValue = if (player.isPlaying.value) 1f else 0.85f,
        animationSpec = androidx.compose.animation.core.spring(stiffness = 350f),
        label = "nowPlayingPulse"
    ).value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // HEADER (centered)
        Box(modifier = Modifier.fillMaxWidth()) {
            TextButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text("← Back")
            }

            Text(
                text = "Now playing",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Center)
            )
        }


        // MEDIA SECTION
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            PremiumAlbumArt(
                artworkUri = player.artworkUri.value,
                isPlaying = player.isPlaying.value,
                pulse = playPulse
            )


            Text(
                text = player.currentTitle.value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Unknown Artist",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            NowPlayingSlider(player)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedIconButton(
                    onClick = { player.previous() },
                    icon = Icons.Filled.SkipPrevious,
                    enabled = true
                )

                Spacer(modifier = Modifier.width(12.dp))

                FilledIconButton(
                    onClick = {
                        if (player.isPlaying.value) player.pause() else player.resume()
                    },
                    icon = if (player.isPlaying.value) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    accent = true
                )

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedIconButton(
                    onClick = { player.next() },
                    icon = Icons.Filled.SkipNext,
                    enabled = true
                )
            }

        }

        // TAB SWITCHER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TabButton(
                text = "lyrics",
                selected = selectedTab == "lyrics"
            ) {
                selectedTab = "lyrics"
            }

            TabButton(
                text = "queue",
                selected = selectedTab == "queue"
            ) {
                selectedTab = "queue"
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                "lyrics" -> LyricsPlaceholder()
                "queue" -> QueuePlaceholder(player)
            }
        }
    }
}

@Composable
private fun PremiumAlbumArt(
    artworkUri: String?,
    isPlaying: Boolean,
    pulse: Float
) {

    val borderAlpha by animateFloatAsState(
        targetValue = if (isPlaying) 0.55f else 0.22f,
        animationSpec = androidx.compose.animation.core.tween(300),
        label = "albumGlow"
    )

    Box(
        modifier = Modifier
            .size(250.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(ElevatedSurface)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AriaGold.copy(alpha = borderAlpha),
                            ElevatedSurface.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        // Artwork
        SongArtwork(
            artworkUri = artworkUri,
            modifier = Modifier
                .matchParentSize()
        )

        // subtle vinyl-like rings

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.Black.copy(alpha = 0.25f))
        )



        // pulse scale when playing
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    scaleX = pulse
                    scaleY = pulse
                }
        )
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun NowPlayingSlider(player: AriaPlayerManager) {
    var sliderPosition by remember { mutableStateOf(0f) }

    val duration = player.duration.toFloat().coerceAtLeast(1f)
    val current = player.currentPosition.toFloat()

    LaunchedEffect(current) {
        sliderPosition = current
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            onValueChangeFinished = { player.seekTo(sliderPosition.toLong()) },
            valueRange = 0f..duration,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatTime(player.currentPosition), style = MaterialTheme.typography.labelLarge)
            Text(formatTime(player.duration), style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun OutlinedIconButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, AriaGold.copy(alpha = 0.50f))
    ) {
        Icon(icon, contentDescription = "control")
    }
}

@Composable
private fun FilledIconButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Boolean
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (accent) AriaGold else MaterialTheme.colorScheme.surfaceContainer,
            contentColor = if (accent) Color.Black else MaterialTheme.colorScheme.onSurface
        ),
        contentPadding = PaddingValues(16.dp)
    ) {
        Icon(icon, contentDescription = "control")
    }
}

@Composable
fun LyricsPlaceholder() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Lyrics will appear here", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        Text(
            "Future feature:\n- Synced lyrics (like Spotify)\n- Scroll highlight\n- Offline lyrics cache",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun QueuePlaceholder(player: AriaPlayerManager) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            "Queue",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )

        val titles = player.queueTitles.value
        if (titles.isEmpty()) {
            Text(
                "No queue",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@Column
        }

        titles.forEachIndexed { index, title ->
            Text(
                text = if (index == 0) "▶ $title" else title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (selected) AriaGold else MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Text(
            text = text.uppercase(),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun formatTime(ms: Long): String {
    val sec = ms / 1000
    val min = sec / 60
    val s = sec % 60
    return "%d:%02d".format(min, s)
}

