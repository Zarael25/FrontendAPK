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
import com.example.frontendapk.navigation.AppScreens
import com.example.frontendapk.data.DetalleTicket
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TusTicketsScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val token = prefs.getString("ACCESS_TOKEN", null)

    var tickets by remember { mutableStateOf<List<DetalleTicket>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Activos", "Finalizados", "Cancelados")

    LaunchedEffect(token) {
        if (!token.isNullOrEmpty()) {
            RetrofitClient.apiService.getMisTickets("Bearer $token")
                .enqueue(object : Callback<List<DetalleTicket>> {
                    override fun onResponse(
                        call: Call<List<DetalleTicket>>,
                        response: Response<List<DetalleTicket>>
                    ) {
                        if (response.isSuccessful) {
                            tickets = response.body() ?: emptyList()
                            loading = false
                        } else {
                            errorMessage = "Error: ${response.code()}"
                            loading = false
                        }
                    }

                    override fun onFailure(call: Call<List<DetalleTicket>>, t: Throwable) {
                        errorMessage = "Error: ${t.message}"
                        loading = false
                    }
                })
        } else {
            errorMessage = "Token inválido"
            loading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tus Tickets") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {

            // Pestañas
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (loading) {
                Text("Cargando tickets...")
            } else if (errorMessage != null) {
                Text("Error: $errorMessage")
            } else {
                val estadoFiltro = when (selectedTabIndex) {
                    0 -> "activo"
                    1 -> "finalizado"
                    2 -> "cancelado"
                    else -> ""
                }

                val ticketsFiltrados = tickets.filter {
                    it.estado.equals(estadoFiltro, ignoreCase = true)
                }

                if (ticketsFiltrados.isEmpty()) {
                    Text("No hay tickets ${tabTitles[selectedTabIndex].lowercase()}.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(ticketsFiltrados) { ticket ->
                            val buttonColor = when (ticket.estado.lowercase()) {
                                "activo" -> MaterialTheme.colorScheme.primary
                                "finalizado" -> MaterialTheme.colorScheme.tertiary
                                "cancelado" -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }

                            Button(
                                onClick = {
                                    navController.navigate(AppScreens.DetalleTicketScreen.createRoute(ticket.ticket_id))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                            ) {
                                Column {
                                    Text(text = ticket.negocio_nombre, style = MaterialTheme.typography.titleMedium)
                                    Text(text = ticket.fila_nombre, style = MaterialTheme.typography.bodyMedium)
                                    val fecha = ticket.fecha_hora_registro?.take(10) ?: "Fecha no disponible"
                                    Text(text = "Fecha: $fecha", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}