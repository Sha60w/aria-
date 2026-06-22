package com.aria.aria.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.unit.dp
import com.aria.aria.ui.components.PremiumButton
import com.aria.aria.ui.components.PremiumSurface
import com.aria.aria.ui.theme.AriaGold

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        PremiumSurface(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Settings", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Premium controls (placeholders for now).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        PremiumSurface(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Playback", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(10.dp))

                PremiumButton(text = "Equalizer", onClick = {
                    // Best-effort: ExoPlayer/Media3 session handling isn't wired yet in this app.
                    // Launch the system equalizer UI.
                    val context = androidx.compose.ui.platform.LocalContext.current
                    try {
                        val intent = android.content.Intent(android.media.audiofx.AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                        intent.putExtra(
                            android.media.audiofx.AudioEffect.EXTRA_PACKAGE_NAME,
                            context.packageName
                        )
                        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        // no-op
                    }
                })
                Spacer(modifier = Modifier.height(10.dp))
                PremiumButton(text = "Crossfade (soon)", onClick = {})
            }
        }

        PremiumSurface(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Appearance", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(10.dp))

                PremiumButton(text = "Vinyl Glow (soon)", onClick = {})

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Tip: Tap the Mini Player for an elevated Now Playing screen.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AriaGold
                )
            }
        }
    }
}

