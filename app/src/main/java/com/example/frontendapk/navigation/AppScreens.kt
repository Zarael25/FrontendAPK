package com.example.frontendapk.navigation

sealed class AppScreens(val route:String){
    object SplashScreen: AppScreens(route = "splash_screen")
    object RegisterScreen: AppScreens(route = "register_screen")
}