package com.aria.aria.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aria.aria.ui.navigation.BottomNavItem
import com.aria.aria.ui.navigation.Screen
import com.aria.aria.ui.screens.home.HomeScreen
import com.aria.aria.ui.screens.library.LibraryScreen
import com.aria.aria.ui.screens.search.SearchScreen
import com.aria.aria.ui.screens.settings.SettingsScreen
import com.aria.aria.ui.theme.AriaGold
import com.aria.aria.ui.theme.AriaGray
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.util.UnstableApi
import com.aria.aria.player.PlayerHolder
import com.aria.aria.ui.components.MiniPlayer
import com.aria.aria.ui.screens.NowPlayingScreen
import com.aria.aria.ui.screens.PlaylistScreen
import com.aria.aria.ui.screens.PlaylistDetailScreen
import com.aria.aria.ui.screens.library.AlbumSongsScreen
import com.aria.aria.ui.screens.library.ArtistSongsScreen
import com.aria.aria.ui.screens.library.FolderSongsScreen


@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AriaApp() {

    val navController = rememberNavController()

    val context = LocalContext.current
    val player = PlayerHolder.getPlayer(context)

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Library,
        BottomNavItem.Search,
        BottomNavItem.Playlists,
        BottomNavItem.Settings
    )


    Scaffold(
        bottomBar = {

            Column {
                // Keep mini player visible on all screens except Now Playing.
                val currentRoute =
                    navController.currentBackStackEntryAsState().value?.destination?.route

                if (currentRoute != "now_playing") {
                    MiniPlayer(
                        player = player,
                        onOpenNowPlaying = {
                            navController.navigate("now_playing")
                        }
                    )
                }

                NavigationBar {


                    val currentRoute =
                        navController.currentBackStackEntryAsState()
                            .value?.destination?.route

                    items.forEach { item ->

                        NavigationBarItem(
                            selected = currentRoute == item.route,

                            onClick = {
                                navController.navigate(item.route)
                            },

                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            },

                            label = {
                                Text(item.title)
                            },

                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AriaGold,
                                selectedTextColor = AriaGold,
                                unselectedIconColor = AriaGray,
                                unselectedTextColor = AriaGray
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->

        AnimatedNavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(
                route = Screen.Home.route,
                enterTransition = { com.aria.aria.ui.components.PremiumTransitions.enter() },
                exitTransition = { com.aria.aria.ui.components.PremiumTransitions.exit() },
                popEnterTransition = { com.aria.aria.ui.components.PremiumTransitions.enter() },
                popExitTransition = { com.aria.aria.ui.components.PremiumTransitions.exit() }
            ) { HomeScreen() }

            composable(
                route = Screen.Library.route,
                enterTransition = { com.aria.aria.ui.components.PremiumTransitions.enter() },
                exitTransition = { com.aria.aria.ui.components.PremiumTransitions.exit() },
                popEnterTransition = { com.aria.aria.ui.components.PremiumTransitions.enter() },
                popExitTransition = { com.aria.aria.ui.components.PremiumTransitions.exit() }
            ) { LibraryScreen(navController) }

            composable(
                route = "playlists_tab",
                enterTransition = { com.aria.aria.ui.components.PremiumTransitions.enter() },
                exitTransition = { com.aria.aria.ui.components.PremiumTransitions.exit() },
                popEnterTransition = { com.aria.aria.ui.components.PremiumTransitions.enter() },
                popExitTransition = { com.aria.aria.ui.components.PremiumTransitions.exit() }
            ) {
                PlaylistScreen(onOpenPlaylist = { playlistName ->
                    navController.navigate("playlist_detail/$playlistName")
                })
            }

            composable(
                route = "playlist_detail/{playlistName}",
                enterTransition = { com.aria.aria.ui.components.PremiumTransitions.enter() },
                exitTransition = { com.aria.aria.ui.components.PremiumTransitions.exit() },
                popEnterTransition = { com.aria.aria.ui.components.PremiumTransitions.enter() },
                popExitTransition = { com.aria.aria.ui.components.PremiumTransitions.exit() }
            ) { backStackEntry ->
                val playlistName = backStackEntry.arguments?.getString("playlistName") ?: ""
                PlaylistDetailScreen(
                    playlistName = playlistName,
                    onPlayQueue = { songs, startIndex ->
                        player.setQueue(songs)
                        player.playQueueAt(startIndex, autoplay = true)
                    }
                )
            }

            composable(
                route = Screen.Search.route,
                enterTransition = { com.aria.aria.ui.components.PremiumTransitions.enter() },
                exitTransition = { com.aria.aria.ui.components.PremiumTransitions.exit() },
                popEnterTransition = { com.aria.aria.ui.components.PremiumTransitions.enter() },
                popExitTransition = { com.aria.aria.ui.components.PremiumTransitions.exit() }
            ) {
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

            composable(
                route = Screen.Settings.route,
                enterTransition = { com.aria.aria.ui.components.PremiumTransitions.enter() },
                exitTransition = { com.aria.aria.ui.components.PremiumTransitions.exit() },
                popEnterTransition = { com.aria.aria.ui.components.PremiumTransitions.enter() },
                popExitTransition = { com.aria.aria.ui.components.PremiumTransitions.exit() }
            ) { SettingsScreen() }

            // --- Library drill-down routes ---
            composable(
                route = "artist_songs/{artist}"
            ) { backStackEntry ->
                val artistEncoded = backStackEntry.arguments?.getString("artist") ?: ""
                val artist = java.net.URLDecoder.decode(artistEncoded, "UTF-8")

                // Build full songs list and subset on this screen.
                val allSongs = com.aria.aria.data.MusicScanner().scanSongs(context)
                ArtistSongsScreen(
                    player = player,
                    songs = allSongs,
                    artist = artist,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "album_songs/{album}/{artist}"
            ) { backStackEntry ->
                val albumEncoded = backStackEntry.arguments?.getString("album") ?: ""
                val artistEncoded = backStackEntry.arguments?.getString("artist") ?: ""
                val album = java.net.URLDecoder.decode(albumEncoded, "UTF-8")
                val artist = java.net.URLDecoder.decode(artistEncoded, "UTF-8")

                val allSongs = com.aria.aria.data.MusicScanner().scanSongs(context)
                AlbumSongsScreen(
                    player = player,
                    songs = allSongs,
                    album = album,
                    artist = artist,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "folder_songs/{folder}"
            ) { backStackEntry ->
                val folderEncoded = backStackEntry.arguments?.getString("folder") ?: ""
                val folder = java.net.URLDecoder.decode(folderEncoded, "UTF-8")

                val allSongs = com.aria.aria.data.MusicScanner().scanSongs(context)
                FolderSongsScreen(
                    player = player,
                    songs = allSongs,
                    folderName = folder,
                    extractFolderName = { uri ->
                        try {
                            val path = android.net.Uri.parse(uri).path ?: return@FolderSongsScreen "Unknown"
                            val segments = path.split('/').filter { it.isNotBlank() }
                            if (segments.isEmpty()) return@FolderSongsScreen "Unknown"
                            if (segments.size >= 2) segments[segments.size - 2] else "Root"
                        } catch (_: Exception) {
                            "Unknown"
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }


            composable(
                route = "now_playing",

                enterTransition = { com.aria.aria.ui.components.PremiumTransitions.enter() },
                exitTransition = { com.aria.aria.ui.components.PremiumTransitions.exit() },
                popEnterTransition = { com.aria.aria.ui.components.PremiumTransitions.enter() },
                popExitTransition = { com.aria.aria.ui.components.PremiumTransitions.exit() }
            ) {
                NowPlayingScreen(
                    player = player,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}