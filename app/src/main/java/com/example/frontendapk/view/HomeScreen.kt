package com.example.frontendapk.view

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.TopAppBar
import androidx.compose.ui.platform.LocalContext

@Composable
fun HomeScreen(navController: NavController, nombre: String) {
    // Obtener el contexto
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Inicio") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¡Bienvenido a la app, $nombre!",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = {
                // Eliminar el token de acceso de las SharedPreferences
                val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().remove("ACCESS_TOKEN").apply()

                // Mostrar mensaje de éxito
                Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()

                // Navegar a la pantalla de Login
                navController.navigate("login_screen") {
                    popUpTo("home_screen") { inclusive = true }
                }
            }) {
                Text("Cerrar sesión")
            }
        }
    }
}