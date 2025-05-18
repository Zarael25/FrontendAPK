package com.example.frontendapk.view



import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.frontendapk.data.RetrofitClient
import com.example.frontendapk.data.UserProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(navController: NavController) {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService

    var username by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("") }
    var suscripcion by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("ACCESS_TOKEN", null)

        if (!token.isNullOrEmpty()) {
            apiService.getUserProfile("Bearer $token").enqueue(object: Callback<UserProfileResponse> {
                override fun onResponse(
                    call: Call<UserProfileResponse>,
                    response: Response<UserProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        val profile = response.body()
                        if (profile != null) {
                            username = profile.username
                            correo = profile.correo
                            nombre = profile.nombre
                            estado = profile.estado
                            suscripcion = profile.suscripcion
                        }
                    } else {
                        Log.e("PerfilScreen", "Error HTTP: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                    Log.e("PerfilScreen", "Error en llamada retrofit: ${t.message}")
                }
            })
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil del Usuario") },
                actions = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver atrás"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Nombre: $nombre", style = MaterialTheme.typography.bodyLarge)
            Text("Username: $username", style = MaterialTheme.typography.bodyLarge)
            Text("Correo: $correo", style = MaterialTheme.typography.bodyLarge)
            Text("Estado: $estado", style = MaterialTheme.typography.bodyLarge)
            Text("Suscripción: $suscripcion", style = MaterialTheme.typography.bodyLarge)
        }
    }
}