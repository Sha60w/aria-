package com.aria.aria.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aria.aria.player.AriaPlayerManager

@Composable
fun SongArtworkRow(
    player: AriaPlayerManager,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SongArtwork(
            artworkUri = player.artworkUri.value,
            modifier = Modifier
                .size(44.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(text = player.currentTitle.value)
        }
    }
}

