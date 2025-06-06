package com.example.frontendapk.view

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.frontendapk.data.FilaAtencion
import com.example.frontendapk.data.RetrofitClient
import com.example.frontendapk.navigation.AppScreens
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleFilaScreen(navController: NavController, filaId: Int) {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService
    var fila by remember { mutableStateOf<FilaAtencion?>(null) }
    var cargando by remember { mutableStateOf(true) }

    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val token = prefs.getString("ACCESS_TOKEN", null)

    LaunchedEffect(filaId) {
        if (!token.isNullOrEmpty()) {
            apiService.getFilaPorId("Bearer $token", filaId)
                .enqueue(object : Callback<FilaAtencion> {
                    override fun onResponse(call: Call<FilaAtencion>, response: Response<FilaAtencion>) {
                        if (response.isSuccessful) {
                            fila = response.body()
                        } else {
                            Toast.makeText(context, "Error al cargar fila", Toast.LENGTH_SHORT).show()
                        }
                        cargando = false
                    }
                    override fun onFailure(call: Call<FilaAtencion>, t: Throwable) {
                        Log.e("DetalleFilaScreen", "Error al obtener detalle: ${t.message}")
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
                title = { Text("Detalle de Fila") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        fila?.let { filaData ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Nombre: ${filaData.nombre}")
                Text("Cantidad de Tickets: ${filaData.cantidad_tickets}")
                Text("Visible: ${filaData.visible}")
                Text("Periodo de Atención: ${filaData.periodo_atencion}")
                Text("Apertura: ${filaData.apertura}")
                Text("Finalización: ${filaData.finalizacion}")
                Text("Ticket Actual: ${filaData.numero_ticket_actual}")

                Spacer(modifier = Modifier.height(16.dp))


                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Text("Visible:")
                    Spacer(Modifier.width(8.dp))
                    var switchState by remember { mutableStateOf(filaData.visible) }
                    Switch(
                        checked = switchState,
                        onCheckedChange = { nuevoEstado ->
                            switchState = nuevoEstado
                            val updateMap = mapOf("visible" to nuevoEstado)
                            if (!token.isNullOrEmpty()) {
                                apiService.editarFilaParcial("Bearer $token", filaId, updateMap)
                                    .enqueue(object : Callback<Void> {
                                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                            if (response.isSuccessful) {
                                                Toast.makeText(context, "Estado actualizado a $nuevoEstado", Toast.LENGTH_SHORT).show()
                                                fila = filaData.copy(visible = nuevoEstado)
                                            } else {
                                                Toast.makeText(context, "Error al actualizar estado", Toast.LENGTH_SHORT).show()
                                                switchState = !nuevoEstado // revertir cambio en UI por error
                                            }
                                        }
                                        override fun onFailure(call: Call<Void>, t: Throwable) {
                                            Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
                                            switchState = !nuevoEstado // revertir cambio en UI por error
                                        }
                                    })
                            }
                        }
                    )
                }





                Button(
                    onClick = {
                        // Resetear el ticket actual a 0
                        val updateMap = mapOf("numero_ticket_actual" to 0)
                        if (!token.isNullOrEmpty()) {
                            apiService.editarFilaParcial("Bearer $token", filaId, updateMap)
                                .enqueue(object : Callback<Void> {
                                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                        if (response.isSuccessful) {
                                            Toast.makeText(context, "Conteo de tickets reiniciado", Toast.LENGTH_SHORT).show()
                                            fila = filaData.copy(numero_ticket_actual = 0)
                                        } else {
                                            Toast.makeText(context, "Error al reiniciar conteo", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    override fun onFailure(call: Call<Void>, t: Throwable) {
                                        Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
                                    }
                                })
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("Resetear ticket actual")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        navController.navigate(AppScreens.EditarFilaScreen.createRoute(filaId))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Editar datos")
                }
            }
        } ?: Text("Cargando...", modifier = Modifier.padding(padding))
    }
}