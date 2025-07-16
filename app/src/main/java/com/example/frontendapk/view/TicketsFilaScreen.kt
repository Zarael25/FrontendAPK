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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsFilaScreen(navController: NavController, filaId: Int) {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val token = prefs.getString("ACCESS_TOKEN", null)

    var tickets by remember { mutableStateOf<List<Ticket>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(filaId) {
        if (!token.isNullOrEmpty()) {
            apiService.getTicketsPorFila("Bearer $token", filaId).enqueue(object : Callback<List<Ticket>> {
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
        if (cargando) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tickets) { ticket ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Usuario: ${ticket.nombre_usuario}", style = MaterialTheme.typography.titleMedium)
                            Text("Posición: ${ticket.posicion}")
                            Text("Atención: ${ticket.fecha_hora_atencion}")

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = {
                                    // Aquí irá la lógica para finalizar
                                }) {
                                    Text("Finalizar")
                                }

                                OutlinedButton(onClick = {
                                    // Aquí irá la lógica para cancelar
                                }) {
                                    Text("Cancelar")
                                }
                            }
                        }
                    }
                }

                if (tickets.isEmpty()) {
                    item {
                        Text("No hay tickets para esta fila.", modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}