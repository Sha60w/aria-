package com.aria.aria.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {

    data object Home : BottomNavItem(
        "home",
        "Home",
        Icons.Default.Home
    )

    data object Library : BottomNavItem(
        "library",
        "Library",
        Icons.Default.LibraryMusic
    )

    data object Playlists : BottomNavItem(
        "playlists_tab",
        "Playlists",
        Icons.Default.LibraryMusic
    )


    data object Search : BottomNavItem(
        "search",
        "Search",
        Icons.Default.Search
    )

    data object Settings : BottomNavItem(
        "settings",
        "Settings",
        Icons.Default.Settings
    )
}