package com.aria.aria.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.aria.aria.ui.screens.home.HomeScreen
import com.aria.aria.ui.screens.library.LibraryScreen
import com.aria.aria.ui.screens.search.SearchScreen
import com.aria.aria.ui.screens.settings.SettingsScreen
import android.content.Context
import com.aria.aria.player.AriaPlayerManager

@Composable
fun AriaNavGraph(
    player: AriaPlayerManager,
    context: Context
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {

        composable(Screen.Home.route) {
            HomeScreen()
        }

        composable(Screen.Library.route) {
            LibraryScreen()
        }

        composable(Screen.Search.route) {
            SearchScreen(
                context = context,
                onPlay = { song ->
                    player.playSong(
                        title = song.title,
                        uri = song.uri
                    )
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}