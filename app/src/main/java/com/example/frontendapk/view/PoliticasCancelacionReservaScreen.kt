package com.example.frontendapk.view

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.frontendapk.data.Negocio
import com.example.frontendapk.data.EditarPoliticasRequest
import com.example.frontendapk.data.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoliticasCancelacionReservaScreen(navController: NavController, negocioId: Int) {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService
    val focusManager = LocalFocusManager.current

    var negocio by remember { mutableStateOf<Negocio?>(null) }

    // Estados editables (inicializados luego de cargar negocio)
    var permiteCancelar by remember { mutableStateOf(false) }
    var tiempoLimiteCancelacion by remember { mutableStateOf("") }
    var maximoReservasDiarias by remember { mutableStateOf("") }

    // Cargar datos al iniciar la pantalla
    LaunchedEffect(negocioId) {
        val prefs = context.getSharedPreferences("app_prefs", 0)
        val token = prefs.getString("ACCESS_TOKEN", null)
        if (!token.isNullOrEmpty()) {
            apiService.getNegocioPorId("Bearer $token", negocioId).enqueue(object : Callback<Negocio> {
                override fun onResponse(call: Call<Negocio>, response: Response<Negocio>) {
                    if (response.isSuccessful) {
                        negocio = response.body()
                        negocio?.let {
                            permiteCancelar = it.permite_cancelar
                            tiempoLimiteCancelacion = it.tiempo_limite_cancelacion.toString()
                            maximoReservasDiarias = it.maximo_reservas_diarias.toString()
                        }
                    } else {
                        Log.e("Politicas", "Error al cargar negocio: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Negocio>, t: Throwable) {
                    Log.e("Politicas", "Fallo al cargar negocio: ${t.message}")
                }
            })
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Políticas") },
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
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("Permitir cancelar")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = permiteCancelar,
                    onCheckedChange = { permiteCancelar = it }
                )
            }

            OutlinedTextField(
                value = tiempoLimiteCancelacion,
                onValueChange = { value ->
                    if (value.isEmpty()) {
                        tiempoLimiteCancelacion = ""
                    } else {
                        val intValue = value.toIntOrNull()
                        if (intValue != null && intValue >= 1) {
                            tiempoLimiteCancelacion = intValue.toString()
                        }
                    }
                },
                label = { Text("Tiempo límite cancelación (minutos)") },
                enabled = permiteCancelar,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = maximoReservasDiarias,
                onValueChange = { value ->
                    if (value.isEmpty()) {
                        maximoReservasDiarias = ""
                    } else {
                        val intValue = value.toIntOrNull()
                        if (intValue != null && intValue >= 0) {
                            maximoReservasDiarias = intValue.toString()
                        }
                    }
                },
                label = { Text("Máximo reservas diarias") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    focusManager.clearFocus(force = true)
                    val prefs = context.getSharedPreferences("app_prefs", 0)
                    val token = prefs.getString("ACCESS_TOKEN", null)
                    if (!token.isNullOrEmpty()) {
                        val request = EditarPoliticasRequest(
                            permite_cancelar = permiteCancelar,
                            tiempo_limite_cancelacion = tiempoLimiteCancelacion.toIntOrNull() ?: 0,
                            maximo_reservas_diarias = maximoReservasDiarias.toIntOrNull() ?: 0
                        )
                        apiService.editarPoliticas("Bearer $token", negocioId, request)
                            .enqueue(object : Callback<Map<String, String>> {
                                override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                                    if (response.isSuccessful) {
                                        Log.i("Politicas", "Políticas actualizadas con éxito")
                                        navController.popBackStack()
                                    } else {
                                        Log.e("Politicas", "Error al actualizar políticas: ${response.code()}")
                                    }
                                }

                                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                                    Log.e("Politicas", "Fallo al actualizar políticas: ${t.message}")
                                }
                            })
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar cambios")
            }
        }
    }
}