package com.example.frontendapk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.frontendapk.navigation.AppNavigation
import com.example.frontendapk.ui.theme.FrontendAPKTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()  // Crear controlador de navegación

            // Obtener la ruta actual de la navegación
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route ?: ""

            // Aquí defines qué rutas usarán tema claro
            val adminRoutes = listOf(
                "login_admin_screen",
                "admin_home_screen",
                "listar_usuarios_admin_screen",
                "editar_usuario_admin_screen/{usuarioId}"

                // agrega aquí las rutas que correspondan a la sección admin
            )

            // Detectar si la ruta actual es admin
            val isAdminScreen = adminRoutes.any { currentRoute.contains(it) }

            // Aplicar el tema condicionalmente
            FrontendAPKTheme(adminMode = isAdminScreen) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        navController = navController,
                        paddingValues = innerPadding
                    )
                }
            }
        }
    }
}
