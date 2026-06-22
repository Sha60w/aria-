package com.aria.aria.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import com.aria.aria.R

@Composable
fun SongArtwork(
    artworkUri: String?,
    modifier: Modifier = Modifier,
    fallbackText: String = ""
) {
    if (artworkUri.isNullOrBlank()) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            // Keep it simple: no text fallback to avoid layout jumps.
        }
        return
    }

    AsyncImage(
        model = artworkUri,
        contentDescription = "Song artwork",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
    )
}

@Composable
fun AsyncImage(
    model: String,
    contentDescription: String,
    contentScale: ContentScale,
    modifier: Modifier
){}