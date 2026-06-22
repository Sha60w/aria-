package com.aria.aria.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

/**
 * Kept for compatibility.
 * The actual transitions are implemented in `AriaApp`.
 */
@Composable
fun ScreenTransitionHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    // No-op.
}


