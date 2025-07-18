package com.example.frontendapk.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.frontendapk.view.RegisterScreen
import com.example.frontendapk.view.SplashScreen
import com.example.frontendapk.view.LoginScreen
import com.example.frontendapk.navigation.AppScreens
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import com.example.frontendapk.view.HomeScreen
import com.example.frontendapk.view.PerfilScreen
import com.example.frontendapk.view.TusNegociosScreen
import com.example.frontendapk.view.DetalleNegocioScreen
import com.example.frontendapk.view.RegistroNegocioScreen
import com.example.frontendapk.view.EditarNegocioScreen
import com.example.frontendapk.view.RegistroFilaScreen
import com.example.frontendapk.view.TusFilasScreen
import com.example.frontendapk.view.DetalleFilaScreen
import com.example.frontendapk.view.EditarFilaScreen
import com.example.frontendapk.view.NegociosVerificadosScreen
import com.example.frontendapk.view.FilasVisiblesScreen
import com.example.frontendapk.view.GenerarTicketScreen
import com.example.frontendapk.view.DetalleTicketScreen
import com.example.frontendapk.view.TusTicketsScreen
import com.example.frontendapk.view.TicketsFilaScreen
import com.example.frontendapk.view.PoliticasCancelacionReservaScreen
import com.example.frontendapk.view.LoginAdminScreen
import com.example.frontendapk.view.HomeAdminScreen
import com.example.frontendapk.view.ListarUsuariosAdminScreen
import com.example.frontendapk.view.EditarUsuarioAdminScreen
import com.example.frontendapk.view.ListarNegociosAdminScreen
import com.example.frontendapk.view.EditarNegocioAdminScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

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
            RegisterScreen(navController = navController)  // Register Screen
        }

        composable(AppScreens.LoginScreen.route) {
            LoginScreen(navController = navController)  // Login Screen
        }

        composable("home_screen/{nombre}") { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: "Usuario"
            HomeScreen(navController = navController, nombre = nombre)
        }

        composable(AppScreens.PerfilScreen.route) {
            PerfilScreen(navController = navController)
        }
        composable(AppScreens.TusNegociosScreen.route) {
            TusNegociosScreen(navController = navController)
        }

        composable("detalle_negocio/{negocioId}") { backStackEntry ->
            val negocioId = backStackEntry.arguments?.getString("negocioId")?.toIntOrNull()
            if (negocioId != null) {
                DetalleNegocioScreen(navController, negocioId)
            }
        }

        composable(AppScreens.RegistroNegocioScreen.route) {
            RegistroNegocioScreen(navController)
        }

        composable(
            route = AppScreens.EditarNegocioScreen.route, // ya incluye {negocioId}
            arguments = listOf(navArgument("negocioId") { type = NavType.IntType })
        ) { backStackEntry ->
            val negocioId = backStackEntry.arguments?.getInt("negocioId") ?: 0
            EditarNegocioScreen(navController, negocioId)
        }


        composable(
            route = AppScreens.RegistroFilaScreen.route,
            arguments = listOf(navArgument("negocioId") { type = NavType.IntType })
        ) { backStackEntry ->
            val negocioId = backStackEntry.arguments?.getInt("negocioId") ?: 0
            RegistroFilaScreen(navController = navController, negocioId = negocioId)
        }


        composable(
            route = AppScreens.TusFilasScreen.route,
            arguments = listOf(navArgument("negocioId") { type = NavType.IntType })
        ) { backStackEntry ->
            val negocioId = backStackEntry.arguments?.getInt("negocioId") ?: 0
            TusFilasScreen(navController, negocioId)
        }

        composable(
            route = AppScreens.DetalleFilaScreen.route,
            arguments = listOf(navArgument("filaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val filaId = backStackEntry.arguments?.getInt("filaId") ?: 0
            DetalleFilaScreen(navController, filaId)
        }

        composable(
            route = AppScreens.EditarFilaScreen.route,
            arguments = listOf(navArgument("filaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val filaId = backStackEntry.arguments?.getInt("filaId") ?: 0
            EditarFilaScreen(navController, filaId)
        }


        composable(AppScreens.NegociosVerificadosScreen.route) {
            NegociosVerificadosScreen(navController)
        }

        composable(
            route = AppScreens.FilasVisiblesScreen.route,
            arguments = listOf(navArgument("negocioId") { type = NavType.IntType })
        ) { backStackEntry ->
            val negocioId = backStackEntry.arguments?.getInt("negocioId") ?: return@composable
            FilasVisiblesScreen(navController = navController, negocioId = negocioId)
        }


        composable(
            route = AppScreens.GenerarTicketScreen.route,
            arguments = listOf(navArgument("filaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val filaId = backStackEntry.arguments?.getInt("filaId") ?: 0
            GenerarTicketScreen(navController, filaId)
        }

        composable("detalle_ticket/{ticketId}") { backStackEntry ->
            val ticketId = backStackEntry.arguments?.getString("ticketId")?.toIntOrNull()
            if (ticketId != null) {
                DetalleTicketScreen(navController, ticketId)
            }
        }


        composable(AppScreens.TusTicketsScreen.route) {
            TusTicketsScreen(navController)
        }

        composable(
            route = AppScreens.PoliticasCancelacionReservaScreen.route, // debe estar definido en AppScreens
            arguments = listOf(navArgument("negocioId") { type = NavType.IntType })
        ) { backStackEntry ->
            val negocioId = backStackEntry.arguments?.getInt("negocioId") ?: 0
            PoliticasCancelacionReservaScreen(navController, negocioId)
        }



        composable(
            route = AppScreens.TicketsFilaScreen.route,
            arguments = listOf(navArgument("filaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val filaId = backStackEntry.arguments?.getInt("filaId") ?: return@composable
            TicketsFilaScreen(navController = navController, filaId = filaId)
        }


        composable(AppScreens.LoginAdminScreen.route) {
            LoginAdminScreen(navController)
        }

        composable(AppScreens.HomeAdminScreen.route) {
            HomeAdminScreen(navController)
        }

        composable(AppScreens.ListarUsuariosAdminScreen.route) {
            ListarUsuariosAdminScreen(navController)
        }

        composable(
            route = AppScreens.EditarUsuarioAdminScreen.route,
            arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
        ) { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: 0
            EditarUsuarioAdminScreen(navController = navController, usuarioId = usuarioId)
        }

        composable(AppScreens.ListarNegociosAdminScreen.route) {
            ListarNegociosAdminScreen(navController)
        }

        composable(
            route = AppScreens.EditarNegocioAdminScreen.route,
            arguments = listOf(navArgument("negocioId") { type = NavType.IntType })
        ) { backStackEntry ->
            val negocioId = backStackEntry.arguments?.getInt("negocioId") ?: 0
            EditarNegocioAdminScreen(navController = navController, negocioId = negocioId)
        }



    }
}