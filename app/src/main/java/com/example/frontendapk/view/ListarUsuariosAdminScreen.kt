package com.example.frontendapk.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.material3.CardDefaults
import com.example.frontendapk.data.RetrofitClient
import com.example.frontendapk.data.Usuario
import android.content.Context


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.material3.Text

import androidx.compose.ui.graphics.Color


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListarUsuariosAdminScreen(navController: NavController) {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService

    var usuarios by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchText by remember { mutableStateOf("") }

    fun buscarUsuarios(search: String) {
        isLoading = true
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("ACCESS_TOKEN", null)

        if (token != null) {
            // Suponiendo que tu endpoint acepta filtro con query param 'search' para nombre
            apiService.buscarUsuariosPorNombre("Bearer $token", search)
                .enqueue(object : Callback<List<Usuario>> {
                    override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                        isLoading = false
                        if (response.isSuccessful) {
                            usuarios = response.body() ?: emptyList()
                        } else {
                            errorMessage = "Error HTTP: ${response.code()}"
                        }
                    }

                    override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                        isLoading = false
                        errorMessage = "Error Retrofit: ${t.message}"
                    }
                })
        } else {
            isLoading = false
            errorMessage = "Token no encontrado"
        }
    }

    // Ejecuta búsqueda cada vez que cambia searchText
    LaunchedEffect(searchText) {
        buscarUsuarios(searchText)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Usuarios") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Buscar por nombre") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                singleLine = true
            )

            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.fillMaxWidth().wrapContentWidth())
                }
                errorMessage != null -> {
                    Text(text = errorMessage ?: "Error desconocido", color = MaterialTheme.colorScheme.error)
                }
                usuarios.isEmpty() -> {
                    Text("No se encontraron usuarios.")
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(usuarios) { usuario ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = "Nombre: ${usuario.nombre}", style = MaterialTheme.typography.titleMedium)
                                    Text(text = "Username: ${usuario.username}", style = MaterialTheme.typography.bodyMedium)
                                    Text(text = "Correo: ${usuario.correo}", style = MaterialTheme.typography.bodyMedium)


                                    val estadoTexto = usuario.estado ?: "N/A"
                                    val isSuspendido = estadoTexto.equals("suspendido", ignoreCase = true)

                                    Text(
                                        text = buildAnnotatedString {
                                            append("Estado: ")
                                            withStyle(
                                                style = SpanStyle(
                                                    color = if (isSuspendido) MaterialTheme.colorScheme.error
                                                    else Color.White
                                                )
                                            ) {
                                                append(estadoTexto)
                                            }
                                        },
                                        style = MaterialTheme.typography.bodyMedium
                                    )


                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            // Aquí puedes navegar a pantalla de detalle o revisión de usuario
                                            // navController.navigate("detalle_usuario_screen/${usuario.usuario_id}")
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Revisar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}