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
import com.example.frontendapk.data.RetrofitClient
import com.example.frontendapk.data.FilaRequest // Asegúrate de tener esta clase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroFilaScreen(navController: NavController, negocioId: Int) {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService

    var nombre by remember { mutableStateOf("") }
    var cantidadTickets by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(true) }
    var periodoAtencion by remember { mutableStateOf("") }
    var apertura by remember { mutableStateOf("") }
    var finalizacion by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Fila de Atención") },
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
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre de la fila") }
            )
            OutlinedTextField(
                value = cantidadTickets,
                onValueChange = { cantidadTickets = it },
                label = { Text("Cantidad de tickets") }
            )
            OutlinedTextField(
                value = periodoAtencion,
                onValueChange = { periodoAtencion = it },
                label = { Text("Periodo de atención (hh:mm:ss)") }
            )
            OutlinedTextField(
                value = apertura,
                onValueChange = { apertura = it },
                label = { Text("Hora de apertura (hh:mm:ss)") }
            )
            OutlinedTextField(
                value = finalizacion,
                onValueChange = { finalizacion = it },
                label = { Text("Hora de finalización (hh:mm:ss)") }
            )

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("Visible:")
                Spacer(Modifier.width(8.dp))
                Switch(
                    checked = visible,
                    onCheckedChange = { visible = it }
                )
            }

            Button(
                onClick = {
                    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val token = prefs.getString("ACCESS_TOKEN", null)

                    if (!token.isNullOrEmpty()) {
                        val fila = FilaRequest(
                            nombre = nombre,
                            cantidad_tickets = cantidadTickets.toIntOrNull() ?: 0,
                            visible = visible,
                            periodo_atencion = periodoAtencion,
                            apertura = apertura,
                            finalizacion = finalizacion,
                            negocio = negocioId,
                            numero_ticket_actual = 0
                        )

                        apiService.crearFila("Bearer $token", fila)
                            .enqueue(object : Callback<Unit> {
                                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "Fila registrada correctamente", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    } else {
                                        val errorBody = response.errorBody()?.string()
                                        try {
                                            val errorJson = org.json.JSONObject(errorBody)
                                            val detailMessage = errorJson.getString("detail")
                                            Toast.makeText(context, detailMessage, Toast.LENGTH_LONG).show()
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Error inesperado", Toast.LENGTH_LONG).show()
                                        }
                                        Log.e("RegistroFila", "Error HTTP: ${response.code()} - $errorBody")
                                    }
                                }

                                override fun onFailure(call: Call<Unit>, t: Throwable) {
                                    Log.e("RegistroFila", "Fallo en Retrofit: ${t.message}")
                                    Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
                                }
                            })
                    } else {
                        Toast.makeText(context, "Token no válido", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar Fila")
            }
        }
    }
}
