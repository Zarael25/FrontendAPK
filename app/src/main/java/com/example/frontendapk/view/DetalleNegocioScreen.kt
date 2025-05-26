package com.example.frontendapk.view

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.frontendapk.data.Negocio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleNegocioScreen(navController: NavController, negocioId: Int) {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService
    var negocio by remember { mutableStateOf<Negocio?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("ACCESS_TOKEN", null)

        if (!token.isNullOrEmpty()) {
            apiService.getNegocioPorId("Bearer $token", negocioId).enqueue(object : Callback<Negocio> {
                override fun onResponse(call: Call<Negocio>, response: Response<Negocio>) {
                    if (response.isSuccessful) {
                        negocio = response.body()
                    }
                }

                override fun onFailure(call: Call<Negocio>, t: Throwable) {
                    Log.e("DetalleNegocio", "Error: ${t.message}")
                }
            })
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Negocio") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            negocio?.let {
                Text("Nombre: ${it.nombre}")
                Text("Descripción: ${it.detalle}")
                Text("Dirección: ${it.direccion}")
                Text("Estado: ${it.estado}")
                Text("Categoría: ${it.categoria}")
                Text("Número de referencia: ${it.num_referencia}")
                Text("Documento respaldo: ${it.doc_respaldo ?: "No disponible"}")

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        navController.navigate(AppScreens.EditarNegocioScreen.createRoute(it.negocio_id))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Editar datos")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dentro de tu Column, antes del botón "Eliminar":
                Button(
                    onClick = {
                        navController.navigate(AppScreens.TusFilasScreen.createRoute(negocioId)) // Ajusta esta ruta según tu ruta definida
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver filas de atención")
                }

                Spacer(modifier = Modifier.height(16.dp))  // Espacio entre botones



                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Eliminar",


                    )
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("¿Estás seguro?") },
                        text = { Text("Esta acción eliminará tu negocio.") },
                        confirmButton = {
                            TextButton(onClick = {
                                showDialog = false
                                val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                val token = prefs.getString("ACCESS_TOKEN", null)

                                if (!token.isNullOrEmpty()) {
                                    apiService.ocultarNegocio("Bearer $token", it.negocio_id)
                                        .enqueue(object : Callback<Map<String, String>> {
                                            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                                                if (response.isSuccessful) {
                                                    Log.i("DetalleNegocio", response.body()?.get("detail") ?: "Negocio ocultado")
                                                    navController.navigate(AppScreens.TusNegociosScreen.route) {
                                                        popUpTo(AppScreens.TusNegociosScreen.route) { inclusive = true }
                                                    }
                                                } else {
                                                    Log.e("DetalleNegocio", "Error al ocultar: ${response.code()}")
                                                }
                                            }

                                            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                                                Log.e("DetalleNegocio", "Fallo al ocultar: ${t.message}")
                                            }
                                        })
                                }
                            }) {
                                Text("Confirmar")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }

            } ?: Text("Cargando datos del negocio...")
        }
    }
}