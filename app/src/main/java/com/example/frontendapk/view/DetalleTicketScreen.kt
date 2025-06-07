package com.example.frontendapk.view


import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.frontendapk.data.DetalleTicket
import com.example.frontendapk.data.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleTicketScreen(navController: NavController, ticketId: Int) {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService

    var detalle by remember { mutableStateOf<DetalleTicket?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(ticketId) {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("ACCESS_TOKEN", null)

        if (token != null) {
            apiService.getDetalleTicket(ticketId, "Bearer $token")
                .enqueue(object : Callback<DetalleTicket> {
                    override fun onResponse(call: Call<DetalleTicket>, response: Response<DetalleTicket>) {
                        isLoading = false
                        if (response.isSuccessful) {
                            detalle = response.body()
                        } else {
                            errorMessage = "Error HTTP: ${response.code()}"
                        }
                    }

                    override fun onFailure(call: Call<DetalleTicket>, t: Throwable) {
                        isLoading = false
                        errorMessage = "Error: ${t.message}"
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
                title = { Text("Detalle del Ticket") },
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
                detalle != null -> {
                    Text("Nombre: ${detalle!!.nombre}")
                    Text("Correo: ${detalle!!.correo}")
                    Text("Estado: ${detalle!!.estado}")
                    Text("Fecha de registro: ${detalle!!.fecha_hora_registro}")
                    Text("Fecha de atención: ${detalle!!.fecha_hora_atencion}")
                    Text("Posición: ${detalle!!.posicion}")
                    Text("Fila: ${detalle!!.fila_nombre}")
                    Text("Negocio: ${detalle!!.negocio_nombre}")
                }
            }
        }
    }
}