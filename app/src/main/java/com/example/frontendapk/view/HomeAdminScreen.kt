package com.example.frontendapk.view

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.frontendapk.navigation.AppScreens
@Composable
fun HomeAdminScreen(navController: NavController) {
    val context = LocalContext.current

    // Verificar token al iniciar la pantalla
    LaunchedEffect(Unit) {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("ACCESS_TOKEN", null)

        if (token.isNullOrEmpty()) {
            // Si no hay token, volver al login admin
            navController.navigate("login_admin_screen") {
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Panel Administrador",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = {
                        // Cerrar sesión admin: eliminar token y demás datos
                        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        sharedPreferences.edit()
                            .remove("ACCESS_TOKEN")
                            .remove("REFRESH_TOKEN")
                            .remove("USER_ID")
                            .remove("NOMBRE")
                            .remove("SUSCRIPCION")
                            .remove("TIPO_USUARIO")
                            .apply()

                        Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()

                        navController.navigate("login_admin_screen") {
                            popUpTo("admin_home_screen") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Cerrar sesión")
                }
            }

            // Botones que luego tendrán funcionalidad
            Button(
                onClick = {
                    navController.navigate(AppScreens.ListarUsuariosAdminScreen.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Listar Usuarios")
            }

            Button(
                onClick = {
                    navController.navigate(AppScreens.ListarUsuariosAdminScreen.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Listar Negocios")
            }
        }
    }
}