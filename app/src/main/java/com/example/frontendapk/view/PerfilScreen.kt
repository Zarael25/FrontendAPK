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
import com.example.frontendapk.navigation.AppScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(navController: NavController) {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService

    // Estados para los datos de perfil
    var username by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("") }
    var suscripcion by remember { mutableStateOf("") }
    var suspendidoContador by remember { mutableStateOf(0) }

    val limiteSuspendido = 5


    // Obtener token de SharedPreferences
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val token = prefs.getString("ACCESS_TOKEN", null)

    // Validar si el usuario está autenticado, si no, navegar a LoginScreen
    LaunchedEffect(token) {
        if (token.isNullOrEmpty()) {
            navController.navigate(AppScreens.LoginScreen.route) {
                // Limpiar backstack para que no pueda volver a esta pantalla sin logearse
                popUpTo(0) { inclusive = true }
            }
        } else {
            // Si hay token, hacer la petición para obtener perfil
            apiService.getUserProfile("Bearer $token").enqueue(object : Callback<UserProfileResponse> {
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
                            suspendidoContador = profile.suspendido_contador
                        }
                    } else {
                        // Si el token no es válido o hay error, también redirigir a login
                        navController.navigate(AppScreens.LoginScreen.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }

                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                    // En fallo de conexión también podrías decidir redirigir o mostrar error
                    // Por simplicidad aquí no redirigimos
                }
            })
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil del Usuario") },
                navigationIcon = {
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
            Text("Suspensiones: $suspendidoContador/$limiteSuspendido", style = MaterialTheme.typography.bodyLarge)

            if (suspendidoContador == 4) {
                Text(
                    text = "La próxima vez que canceles un ticket serás suspendido indefinidamente.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }


        }
    }
}