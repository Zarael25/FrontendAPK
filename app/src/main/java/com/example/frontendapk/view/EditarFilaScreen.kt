package com.example.frontendapk.view

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.frontendapk.data.Atencion
import com.example.frontendapk.data.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarFilaScreen(navController: NavController, filaId: Int) {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService

    var nombre by remember { mutableStateOf("") }
    var cantidadTickets by remember { mutableStateOf("") }
    var periodoAtencion by remember { mutableStateOf("") }
    var apertura by remember { mutableStateOf("") }
    var finalizacion by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(true) }

    // Obtener datos actuales
    LaunchedEffect(filaId) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("ACCESS_TOKEN", null)

        if (!token.isNullOrEmpty()) {
            apiService.getFilaPorId("Bearer $token", filaId).enqueue(object : Callback<com.example.frontendapk.data.Atencion> {
                override fun onResponse(call: Call<com.example.frontendapk.data.Atencion>, response: Response<com.example.frontendapk.data.Atencion>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            nombre = it.nombre ?: ""
                            cantidadTickets = it.cantidad_tickets.toString()
                            periodoAtencion = it.periodo_atencion ?: ""
                            apertura = it.apertura ?: ""
                            finalizacion = it.finalizacion ?: ""
                        }
                        cargando = false
                    } else {
                        Toast.makeText(context, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                        cargando = false
                    }
                }

                override fun onFailure(call: Call<com.example.frontendapk.data.Atencion>, t: Throwable) {
                    Log.e("EditarFila", "Error: ${t.message}")
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
                title = { Text("Editar Fila") },
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
            OutlinedTextField(value = cantidadTickets, onValueChange = { cantidadTickets = it.filter { c -> c.isDigit() } }, label = { Text("Cantidad de Tickets") })
            OutlinedTextField(value = periodoAtencion, onValueChange = { periodoAtencion = it }, label = { Text("Periodo de Atención (HH:mm:ss)") })
            OutlinedTextField(value = apertura, onValueChange = { apertura = it }, label = { Text("Hora de Apertura (HH:mm:ss)") })
            OutlinedTextField(value = finalizacion, onValueChange = { finalizacion = it }, label = { Text("Hora de Finalización (HH:mm:ss)") })

            Button(
                onClick = {
                    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val token = prefs.getString("ACCESS_TOKEN", null)

                    if (!token.isNullOrEmpty()) {
                        val datos = mutableMapOf<String, Any>()

                        datos["nombre"] = nombre
                        datos["cantidad_tickets"] = cantidadTickets.toIntOrNull() ?: 0
                        datos["periodo_atencion"] = periodoAtencion
                        datos["apertura"] = apertura
                        datos["finalizacion"] = finalizacion

                        apiService.editarFilaParcial("Bearer $token", filaId, datos)
                            .enqueue(object : Callback<Void> {
                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "Fila actualizada correctamente", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    } else {
                                        Log.e("EditarFila", "Error HTTP: ${response.code()}")
                                        Toast.makeText(context, "Error al actualizar la fila", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    Log.e("EditarFila", "Fallo en Retrofit: ${t.message}")
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