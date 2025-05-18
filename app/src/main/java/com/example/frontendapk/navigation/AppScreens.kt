package com.example.frontendapk.navigation

sealed class AppScreens(val route:String){
    object SplashScreen: AppScreens(route = "splash_screen")
    object RegisterScreen: AppScreens(route = "register_screen")
    object LoginScreen : AppScreens(route = "login_screen")
    object PerfilScreen : AppScreens(route = "perfil_screen")
}