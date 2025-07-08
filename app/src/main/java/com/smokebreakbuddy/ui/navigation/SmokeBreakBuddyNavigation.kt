package com.smokebreakbuddy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smokebreakbuddy.ui.screens.*

object Destinations {
    const val AUTH_SCREEN = "auth"
    const val MAIN_SCREEN = "main"
}

@Composable
fun SmokeBreakBuddyNavigation(isLoggedIn: Boolean) {
    val navController = rememberNavController()
    val startDestination = remember { if (isLoggedIn) Destinations.MAIN_SCREEN else Destinations.AUTH_SCREEN }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Destinations.AUTH_SCREEN) { AuthScreen(navController) }
        composable(Destinations.MAIN_SCREEN) { MainScreen(navController) }
    }
}
