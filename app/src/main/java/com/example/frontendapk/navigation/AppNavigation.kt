package com.example.frontendapk.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.frontendapk.view.RegisterScreen
import com.example.frontendapk.view.SplashScreen
import com.example.frontendapk.navigation.AppScreens
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
@Composable
fun AppNavigation(navController: NavHostController, paddingValues: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = AppScreens.SplashScreen.route,
        modifier = Modifier.padding(paddingValues)

    ) {
        composable(AppScreens.SplashScreen.route) {
            SplashScreen(navController = navController)  // Splash Screen
        }
        composable(AppScreens.RegisterScreen.route) {
            RegisterScreen()  // Register Screen
        }
    }
}