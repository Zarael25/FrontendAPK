package com.example.frontendapk.view

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.frontendapk.data.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.frontendapk.navigation.AppScreens
import androidx.compose.foundation.shape.CircleShape
import com.example.frontendapk.data.Negocio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NegociosVerificadosScreen(navController: NavController) {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService

    var negocios by remember { mutableStateOf<List<Negocio>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchText by remember { mutableStateOf("") }

    fun buscarNegocios(search: String) {
        isLoading = true
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("ACCESS_TOKEN", null)

        if (token != null) {
            apiService.getNegociosVerificados("Bearer $token", search)
                .enqueue(object : Callback<List<Negocio>> {
                    override fun onResponse(call: Call<List<Negocio>>, response: Response<List<Negocio>>) {
                        isLoading = false
                        if (response.isSuccessful) {
                            negocios = response.body() ?: emptyList()
                        } else {
                            errorMessage = "Error HTTP: ${response.code()}"
                        }
                    }

                    override fun onFailure(call: Call<List<Negocio>>, t: Throwable) {
                        isLoading = false
                        errorMessage = "Error Retrofit: ${t.message}"
                    }
                })
        } else {
            isLoading = false
            errorMessage = "Token no encontrado"
        }
    }



    LaunchedEffect(searchText) {
        buscarNegocios(searchText)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Negocios Verificados") },
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
            // Buscador
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Buscar negocio") },
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
                    Text(
                        text = errorMessage ?: "Error desconocido",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                negocios.isEmpty() -> {
                    Text("No hay negocios verificados.")
                }

                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(negocios) { negocio ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = negocio.nombre, style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Dirección: ${negocio.direccion}", style = MaterialTheme.typography.bodyMedium)
                                    Text(text = "Categoría: ${negocio.categoria}", style = MaterialTheme.typography.bodyMedium)
                                    Text(text = negocio.detalle, style = MaterialTheme.typography.bodySmall)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            navController.navigate(AppScreens.FilasVisiblesScreen.createRoute(negocio.negocio_id))
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Ver Filas de Atención")
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
