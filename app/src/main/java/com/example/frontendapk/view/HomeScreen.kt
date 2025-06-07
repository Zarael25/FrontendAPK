package com.example.frontendapk.view

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.LaunchedEffect
import com.example.frontendapk.navigation.AppScreens

@Composable
fun HomeScreen(navController: NavController, nombre: String) {
    val context = LocalContext.current

    // Verificar si hay token al iniciar la pantalla
    LaunchedEffect(Unit) {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("ACCESS_TOKEN", null)

        if (token.isNullOrEmpty()) {
            // Si no hay token, redirigir a LoginScreen
            navController.navigate("login_screen") {
                // Limpia el stack para que no pueda volver a Home sin loguearse
                popUpTo(0)
            }
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tu UI aquí, igual que antes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filas Virtuales",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Row {
                    Button(
                        onClick = {
                            navController.navigate("perfil_screen")
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Perfil")
                    }

                    Button(
                        onClick = {
                            // Cerrar sesión: eliminar token
                            val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            sharedPreferences.edit().remove("ACCESS_TOKEN").apply()

                            Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()

                            navController.navigate("login_screen") {
                                popUpTo("home_screen") { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("X")
                    }
                }
            }

            Text(
                text = "Bienvenid@ $nombre. Esta aplicación te permite gestionar y participar en filas virtuales para diferentes negocios de forma eficiente.",
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Button(
                onClick = { navController.navigate(AppScreens.TusTicketsScreen.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Ver Tickets")
            }

            Button(
                onClick = { navController.navigate(AppScreens.NegociosVerificadosScreen.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Ver negocios")
            }

            Button(
                onClick = { navController.navigate("tus_negocios_screen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Tus negocios")
            }
        }
    }
}