package com.example.frontendapk.view

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.frontendapk.data.RetrofitClient
import com.example.frontendapk.data.Ticket
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import android.app.DatePickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsFilaScreen(navController: NavController, filaId: Int) {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val token = prefs.getString("ACCESS_TOKEN", null)

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    var fecha by remember { mutableStateOf(LocalDate.now().format(dateFormatter)) }

    var tickets by remember { mutableStateOf<List<Ticket>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var ticketSeleccionado by remember { mutableStateOf<Ticket?>(null) }
    var estadoSeleccionado by remember { mutableStateOf("") }

    fun cargarTicketsPorFecha() {
        if (!token.isNullOrEmpty()) {
            cargando = true
            apiService.getTicketsPorFilaConFecha("Bearer $token", filaId, fecha)
                .enqueue(object : Callback<List<Ticket>> {
                    override fun onResponse(call: Call<List<Ticket>>, response: Response<List<Ticket>>) {
                        if (response.isSuccessful) {
                            tickets = response.body()?.sortedBy { it.posicion } ?: emptyList()
                        } else {
                            Toast.makeText(context, "Error al cargar tickets", Toast.LENGTH_SHORT).show()
                        }
                        cargando = false
                    }

                    override fun onFailure(call: Call<List<Ticket>>, t: Throwable) {
                        Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
                        cargando = false
                    }
                })
        }
    }

    // Cargar tickets al inicio y cada vez que cambia la fecha
    LaunchedEffect(filaId, fecha) {
        cargarTicketsPorFecha()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tickets de la Fila") },
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
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Selector de fecha (manual, formato YYYY-MM-DD)
            val dateParts = fecha.split("-").map { it.toInt() }
            val year = dateParts[0]
            val month = dateParts[1] - 1 // DatePickerDialog usa 0-based para el mes
            val day = dateParts[2]

            val datePickerDialog = remember {
                DatePickerDialog(
                    context,
                    { _, y, m, d ->
                        val selectedDate = LocalDate.of(y, m + 1, d)
                        fecha = selectedDate.format(dateFormatter)
                    },
                    year, month, day
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Fecha: $fecha", style = MaterialTheme.typography.titleMedium)
                Button(onClick = { datePickerDialog.show() }) {
                    Text("Seleccionar fecha")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (cargando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(tickets) { ticket ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Usuario: ${ticket.nombre_usuario}", style = MaterialTheme.typography.titleMedium)
                                Text("Posición: ${ticket.posicion}")
                                Text("Atención: ${ticket.fecha_hora_atencion}")
                                Text("Estado: ${ticket.estado}")

                                Spacer(modifier = Modifier.height(8.dp))

                                val habilitado = ticket.estado == "activo"

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            ticketSeleccionado = ticket
                                            estadoSeleccionado = "finalizado"
                                            showDialog = true
                                        },
                                        enabled = habilitado
                                    ) {
                                        Text("Finalizar")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            ticketSeleccionado = ticket
                                            estadoSeleccionado = "cancelado"
                                            showDialog = true
                                        },
                                        enabled = habilitado
                                    ) {
                                        Text("Cancelar")
                                    }
                                }
                            }
                        }
                    }

                    if (tickets.isEmpty()) {
                        item {
                            Text("No hay tickets para esta fecha.", modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación
    if (showDialog && ticketSeleccionado != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar acción") },
            text = { Text("¿Estás seguro de querer marcar este ticket como '$estadoSeleccionado'?") },
            confirmButton = {
                TextButton(onClick = {
                    val id = ticketSeleccionado!!.ticket_id
                    val estado = mapOf("nuevo_estado" to estadoSeleccionado)

                    if (!token.isNullOrEmpty()) {
                        apiService.cambiarEstadoTicket("Bearer $token", id, estado)
                            .enqueue(object : Callback<Void> {
                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "Estado actualizado", Toast.LENGTH_SHORT).show()
                                        cargarTicketsPorFecha()
                                    } else {
                                        Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
                                    }
                                    showDialog = false
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
                                    showDialog = false
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
}