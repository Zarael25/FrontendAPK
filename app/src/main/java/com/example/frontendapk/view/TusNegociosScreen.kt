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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.frontendapk.navigation.AppScreens
import androidx.compose.foundation.shape.CircleShape
import com.example.frontendapk.data.Negocio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TusNegociosScreen(navController: NavController) {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService

    var negocios by remember { mutableStateOf<List<Negocio>>(emptyList()) }

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("ACCESS_TOKEN", null)

        if (!token.isNullOrEmpty()) {
            apiService.getMisNegocios("Bearer $token").enqueue(object : Callback<List<Negocio>> {
                override fun onResponse(call: Call<List<Negocio>>, response: Response<List<Negocio>>) {
                    if (response.isSuccessful) {
                        negocios = response.body()?.filter { it.estado != "oculto" } ?: emptyList()
                    } else {
                        Log.e("TusNegociosScreen", "Error HTTP: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<Negocio>>, t: Throwable) {
                    Log.e("TusNegociosScreen", "Error en llamada Retrofit: ${t.message}")
                }
            })
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tus Negocios") },
                actions = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver atrás")
                    }
                }
            )
        },
        floatingActionButton = {
            // Botón flotante redondo en la esquina inferior izquierda
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                FloatingActionButton(
                    onClick = { navController.navigate(AppScreens.RegistroNegocioScreen.route) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(androidx.compose.ui.Alignment.BottomEnd), // esquina inferior izquierda
                    shape = CircleShape
                ) {
                    Text("+", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (negocios.isEmpty()) {
                Text("No tienes negocios registrados.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(negocios) { negocio ->
                        val color = when (negocio.estado) {
                            "verificado" -> MaterialTheme.colorScheme.primary
                            "en_revision" -> MaterialTheme.colorScheme.tertiary
                            "rechazado" -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onBackground
                        }

                        Button(
                            onClick = {
                                navController.navigate(AppScreens.DetalleNegocioScreen.createRoute(negocio.negocio_id))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = color),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(negocio.nombre)
                        }
                    }
                }
            }
        }
    }
}