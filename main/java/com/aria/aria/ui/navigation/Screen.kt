package com.aria.aria.ui.navigation

sealed class Screen(
    val route: String
){
    data object Home : Screen("home")
    data object Library : Screen("library")
    data object Search : Screen("search")
    data object Settings : Screen("settings")
}
