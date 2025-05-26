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
import com.example.frontendapk.data.Atencion
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TusFilasScreen(navController: NavController, negocioId: Int) {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService
    var filas by remember { mutableStateOf<List<Atencion>>(emptyList()) }

    // Token desde preferencias
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val token = prefs.getString("ACCESS_TOKEN", null)

    // Obtener filas del negocio
    LaunchedEffect(negocioId) {
        if (!token.isNullOrEmpty()) {
            apiService.getFilasPorNegocio("Bearer $token", negocioId)
                .enqueue(object : Callback<List<Atencion>> {
                    override fun onResponse(call: Call<List<Atencion>>, response: Response<List<Atencion>>) {
                        if (response.isSuccessful) {
                            filas = response.body() ?: emptyList()
                        }
                    }

                    override fun onFailure(call: Call<List<Atencion>>, t: Throwable) {
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
                FloatingActionButton(
                    onClick = {
                        navController.navigate(AppScreens.RegistroFilaScreen.createRoute(negocioId))
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(androidx.compose.ui.Alignment.BottomEnd),
                    shape = CircleShape
                ) {
                    Text(
                        "+",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary
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
                            onClick = { navController.navigate(AppScreens.DetalleFilaScreen.createRoute(fila.atencion_id))
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