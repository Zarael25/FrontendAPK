package com.example.frontendapk.view

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.frontendapk.data.NegocioRequest
import com.example.frontendapk.data.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarNegocioScreen(navController: NavController, negocioId: Int) {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService

    var nombre by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var numReferencia by remember { mutableStateOf("") }
    var detalle by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(negocioId) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("ACCESS_TOKEN", null)

        if (!token.isNullOrEmpty()) {
            apiService.getNegocioPorId("Bearer $token", negocioId).enqueue(object : Callback<com.example.frontendapk.data.Negocio> {
                override fun onResponse(call: Call<com.example.frontendapk.data.Negocio>, response: Response<com.example.frontendapk.data.Negocio>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            nombre = it.nombre
                            direccion = it.direccion
                            categoria = it.categoria
                            numReferencia = it.num_referencia
                            detalle = it.detalle
                        }
                        cargando = false
                    } else {
                        Toast.makeText(context, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                        cargando = false
                    }
                }

                override fun onFailure(call: Call<com.example.frontendapk.data.Negocio>, t: Throwable) {
                    Log.e("EditarNegocio", "Error: ${t.message}")
                    Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
                    cargando = false
                }
            })
        }
    }

    if (cargando) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Negocio") },
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
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
            OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") })
            //OutlinedTextField(value = categoria, onValueChange = { categoria = it }, label = { Text("Categoría") })
            OutlinedTextField(value = numReferencia, onValueChange = { numReferencia = it }, label = { Text("Número de Referencia") })
            OutlinedTextField(
                value = detalle,
                onValueChange = { detalle = it },
                label = { Text("Detalle") },
                modifier = Modifier.height(100.dp)
            )

            Button(
                onClick = {
                    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val token = prefs.getString("ACCESS_TOKEN", null)

                    if (!token.isNullOrEmpty()) {
                        val negocioUpdate = NegocioRequest(
                            nombre = nombre,
                            direccion = direccion,
                            categoria = categoria,
                            num_referencia = numReferencia,
                            detalle = detalle
                        )

                        apiService.editarNegocioParcial("Bearer $token", negocioId, negocioUpdate)
                            .enqueue(object : Callback<com.example.frontendapk.data.Negocio> {
                                override fun onResponse(
                                    call: Call<com.example.frontendapk.data.Negocio>,
                                    response: Response<com.example.frontendapk.data.Negocio>
                                ) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "Negocio actualizado correctamente", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack() // Volver atrás
                                    } else {
                                        Log.e("EditarNegocio", "Error HTTP: ${response.code()}")
                                        Toast.makeText(context, "Error al actualizar el negocio", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<com.example.frontendapk.data.Negocio>, t: Throwable) {
                                    Log.e("EditarNegocio", "Fallo en Retrofit: ${t.message}")
                                    Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
                                }
                            })
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar cambios")
            }
        }
    }
}
