package com.example.frontendapk.navigation

sealed class AppScreens(val route: String) {
    object SplashScreen : AppScreens(route = "splash_screen")
    object RegisterScreen : AppScreens(route = "register_screen")
    object LoginScreen : AppScreens(route = "login_screen")
    object PerfilScreen : AppScreens(route = "perfil_screen")
    object TusNegociosScreen : AppScreens(route = "tus_negocios_screen")
    object DetalleNegocioScreen : AppScreens("detalle_negocio/{negocioId}") {
        fun createRoute(negocioId: Int) = "detalle_negocio/$negocioId"
    }
    object RegistroNegocioScreen : AppScreens("registro_negocio")

    object EditarNegocioScreen : AppScreens("editar_negocio/{negocioId}") {
        fun createRoute(negocioId: Int) = "editar_negocio/$negocioId"
    }

}