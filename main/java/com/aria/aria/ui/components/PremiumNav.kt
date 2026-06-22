package com.aria.aria.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset

object PremiumNav {
    private const val Dur = 220

    fun enter(): EnterTransition = fadeIn(animationSpec = tween(Dur)) +
            slideInVertically(
                animationSpec = tween(Dur),
                initialOffsetY = { fullHeight -> fullHeight / 14 }
            )

    fun exit(): ExitTransition = fadeOut(animationSpec = tween(Dur)) +
            slideOutVertically(
                animationSpec = tween(Dur),
                targetOffsetY = { fullHeight -> -fullHeight / 14 }
            )
}

@Composable
fun PremiumAnimatedScreen(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = PremiumNav.enter(),
        exit = PremiumNav.exit()
    ) {
        content()
    }
}

