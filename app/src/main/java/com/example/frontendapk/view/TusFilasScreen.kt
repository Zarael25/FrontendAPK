package com.example.frontendapk.view

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.navigation.NavController
import com.example.frontendapk.navigation.AppScreens
import com.example.frontendapk.data.RetrofitClient
import com.example.frontendapk.data.FilaAtencion
import com.example.frontendapk.data.Negocio
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Toast


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TusFilasScreen(navController: NavController, negocioId: Int) {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService
    var filas by remember { mutableStateOf<List<FilaAtencion>>(emptyList()) }
    var estadoNegocio by remember { mutableStateOf<String?>(null) }



    // Token desde preferencias
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val token = prefs.getString("ACCESS_TOKEN", null)

    // Obtener filas del negocio
    LaunchedEffect(negocioId) {
        if (!token.isNullOrEmpty()) {
            // Obtener estado del negocio
            apiService.getNegocioPorId("Bearer $token", negocioId)
                .enqueue(object : Callback<Negocio> {
                    override fun onResponse(call: Call<Negocio>, response: Response<Negocio>) {
                        if (response.isSuccessful) {
                            estadoNegocio = response.body()?.estado
                        }
                    }

                    override fun onFailure(call: Call<Negocio>, t: Throwable) {
                        Log.e("TusFilasScreen", "Error al obtener negocio: ${t.message}")
                    }
                })


            apiService.getFilasPorNegocio("Bearer $token", negocioId)
                .enqueue(object : Callback<List<FilaAtencion>> {
                    override fun onResponse(call: Call<List<FilaAtencion>>, response: Response<List<FilaAtencion>>) {
                        if (response.isSuccessful) {
                            filas = response.body() ?: emptyList()
                        }
                    }

                    override fun onFailure(call: Call<List<FilaAtencion>>, t: Throwable) {
                        Log.e("TusFilasScreen", "Error al obtener filas: ${t.message}")
                    }
                })
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tus Filas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                val esVerificado = estadoNegocio == "verificado"
                FloatingActionButton(
                    onClick = {
                        if (esVerificado) {
                            navController.navigate(AppScreens.RegistroFilaScreen.createRoute(negocioId))
                        }
                        else {
                            Toast.makeText(context, "Solo los negocios verificados pueden crear filas", Toast.LENGTH_SHORT).show()
                        }
                    },
                    containerColor = if (esVerificado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.align(androidx.compose.ui.Alignment.BottomEnd),
                    shape = CircleShape
                ) {
                    Text(
                        "+",
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (esVerificado) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (filas.isEmpty()) {
                Text("Este negocio no tiene filas registradas.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filas) { fila ->
                        Button(
                            onClick = { navController.navigate(AppScreens.DetalleFilaScreen.createRoute(fila.fila_atencion_id))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (fila.visible) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.tertiary
                            )
                        ) {
                            Text(fila.nombre)
                        }
                    }
                }
            }
        }
    }
}