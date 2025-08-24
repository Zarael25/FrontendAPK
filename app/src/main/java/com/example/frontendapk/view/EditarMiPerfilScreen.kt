package com.example.frontendapk.view


import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.frontendapk.data.RetrofitClient
import com.example.frontendapk.data.EditarPerfilRequest
import com.example.frontendapk.data.UserProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarMiPerfilScreen(navController: NavController) {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService

    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val token = prefs.getString("ACCESS_TOKEN", null) ?: ""

    var username by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }

    // Estado para mostrar el AlertDialog
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    LaunchedEffect(token) {
        if (token.isNotEmpty()) {
            apiService.getUserProfile("Bearer $token").enqueue(object : Callback<UserProfileResponse> {
                override fun onResponse(
                    call: Call<UserProfileResponse>,
                    response: Response<UserProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { perfil ->
                            username = perfil.username
                            nombre = perfil.nombre
                            correo = perfil.correo
                        }
                    } else {
                        Toast.makeText(context, "No se pudo cargar perfil", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                    Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver atrás")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { mostrarConfirmacion = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }

            // Dialogo de confirmación
            if (mostrarConfirmacion) {
                AlertDialog(
                    onDismissRequest = { mostrarConfirmacion = false },
                    title = { Text("Confirmación") },
                    text = { Text("¿Deseas guardar los cambios de tu perfil?") },
                    confirmButton = {
                        TextButton(onClick = {
                            mostrarConfirmacion = false

                            // Enviar PATCH al backend
                            val body = EditarPerfilRequest(username, nombre, correo)
                            apiService.editarPerfil("Bearer $token", body)
                                .enqueue(object : Callback<UserProfileResponse> {
                                    override fun onResponse(
                                        call: Call<UserProfileResponse>,
                                        response: Response<UserProfileResponse>
                                    ) {
                                        if (response.isSuccessful) {
                                            Toast.makeText(context, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                                            navController.popBackStack()
                                        } else {
                                            Toast.makeText(context, "Error al actualizar perfil", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                                        Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
                                    }
                                })

                        }) {
                            Text("Sí")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarConfirmacion = false }) {
                            Text("No")
                        }
                    }
                )
            }
        }
    }
}
