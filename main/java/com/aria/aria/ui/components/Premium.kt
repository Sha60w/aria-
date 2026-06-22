package com.aria.aria.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.fadeOut



import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.rotate

import androidx.compose.material3.Card

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aria.aria.ui.theme.AriaGold
import com.aria.aria.ui.theme.ElevatedSurface
import com.aria.aria.ui.theme.MatteBlack
import androidx.compose.ui.draw.shadow

@Composable
fun PremiumSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        color = ElevatedSurface,
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 6.dp
    ) {
        content()
    }
}

@Composable
fun PremiumButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    onClick: @Composable () -> Unit
) {
    // Indented / premium button: subtle shadow + gold stroke
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .shadow(elevation = 10.dp, shape = RoundedCornerShape(14.dp), ambientColor = AriaGold.copy(0.15f))
            .background(Color.Transparent)
            .clickable(enabled = enabled, onClick = onClick as () -> Unit)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, AriaGold.copy(alpha = 0.55f)),

            colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = MatteBlack.copy(alpha = 0.75f))
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = text,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun PremiumIconChip(
    icon: ImageVector,
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable(onClick = onClick),
        tonalElevation = if (selected) 4.dp else 0.dp,
        border = if (selected) androidx.compose.foundation.BorderStroke(1.dp, AriaGold.copy(alpha = 0.65f)) else null,
        color = if (selected) MatteBlack.copy(alpha = 0.9f) else ElevatedSurface,
        shape = RoundedCornerShape(999.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = if (selected) AriaGold else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = text,
                modifier = Modifier.padding(start = 8.dp),
                color = if (selected) AriaGold else MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun PremiumAnimatedContent(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter =
            fadeIn(
                animationSpec = spring(
                    stiffness = Spring.StiffnessMediumLow
                )
            ) +
                    slideInVertically(
                        animationSpec = spring(
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ) { it / 2 } +
                    scaleIn(
                        animationSpec = spring(
                            stiffness = Spring.StiffnessMediumLow
                        ),
                        initialScale = 0.98f
                    ),
        exit = fadeOut()
    ) {
        content()
    }
}

