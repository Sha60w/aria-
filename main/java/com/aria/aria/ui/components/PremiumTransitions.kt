package com.aria.aria.ui.components

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import androidx.navigation.compose.rememberNavController

/**
 * Navigation transitions require accompanist navigation-animation.
 *
 * If the dependency is not present, we will only use AnimatedVisibility-based
 * animations inside screens.
 */
object PremiumTransitions {
    private const val DefaultDurationMs = 220

    fun enter(): androidx.compose.animation.EnterTransition = fadeIn(animationSpec = tween(DefaultDurationMs))

    fun exit(): androidx.compose.animation.ExitTransition = fadeOut(animationSpec = tween(DefaultDurationMs))
}


