package com.example.frontendapk.view


import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.frontendapk.data.FilaAtencion
import com.example.frontendapk.data.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.example.frontendapk.navigation.AppScreens
import com.example.frontendapk.data.TicketGenerado
import android.widget.Toast
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilasVisiblesScreen(navController: NavController, negocioId: Int) {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService

    var filas by remember { mutableStateOf<List<FilaAtencion>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }


    var showDialog by remember { mutableStateOf(false) }
    var filaSeleccionada by remember { mutableStateOf<FilaAtencion?>(null) }


    LaunchedEffect(Unit) {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("ACCESS_TOKEN", null)

        if (token != null) {
            apiService.getFilasVisibles(negocioId, "Bearer $token")
                .enqueue(object : Callback<List<FilaAtencion>> {
                    override fun onResponse(call: Call<List<FilaAtencion>>, response: Response<List<FilaAtencion>>) {
                        isLoading = false
                        if (response.isSuccessful) {
                            filas = response.body() ?: emptyList()
                        } else {
                            errorMessage = "Error HTTP: ${response.code()}"
                        }
                    }

                    override fun onFailure(call: Call<List<FilaAtencion>>, t: Throwable) {
                        isLoading = false
                        errorMessage = "Error Retrofit: ${t.message}"
                    }
                })
        } else {
            isLoading = false
            errorMessage = "Token no encontrado"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filas visibles") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            when {
                isLoading -> CircularProgressIndicator()
                errorMessage != null -> Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                filas.isEmpty() -> Text("No hay filas visibles.")
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(filas) { fila ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Nombre: ${fila.nombre}")
                                    Text("Horario: ${fila.apertura} - ${fila.finalizacion}")
                                    Text("Periodo: ${fila.periodo_atencion}")
                                    Text("Tickets: ${fila.numero_ticket_actual}/${fila.cantidad_tickets}")

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(
                                        onClick = {
                                            filaSeleccionada = fila
                                            showDialog = true
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Obtener ticket")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // AlertDialog para confirmar
    if (showDialog && filaSeleccionada != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar") },
            text = { Text("¿Deseas obtener un ticket para esta fila?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        val token = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            .getString("ACCESS_TOKEN", null)

                        if (token != null) {
                            val body = mapOf("fila_atencion" to filaSeleccionada!!.fila_atencion_id)

                            RetrofitClient.apiService.generarTicket(body, "Bearer $token")
                                .enqueue(object : Callback<TicketGenerado> {
                                    override fun onResponse(call: Call<TicketGenerado>, response: Response<TicketGenerado>) {
                                        if (response.isSuccessful) {
                                            val ticketId = response.body()?.ticket_id
                                            if (ticketId != null) {
                                                navController.navigate(AppScreens.DetalleTicketScreen.createRoute(ticketId))
                                            }
                                        } else {
                                            val errorBody = response.errorBody()?.string()
                                            if (!errorBody.isNullOrEmpty()) {
                                                try {
                                                    val errorJson = JSONObject(errorBody)
                                                    Toast.makeText(context, errorJson.optString("error", "Error al generar ticket."), Toast.LENGTH_LONG).show()
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Error inesperado.", Toast.LENGTH_LONG).show()
                                                }
                                            } else {
                                                Toast.makeText(context, "No se pudo leer el error.", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }

                                    override fun onFailure(call: Call<TicketGenerado>, t: Throwable) {
                                        Log.e("GenerarTicket", "Error: ${t.message}")
                                    }
                                })
                        }
                    }
                ) {
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
}