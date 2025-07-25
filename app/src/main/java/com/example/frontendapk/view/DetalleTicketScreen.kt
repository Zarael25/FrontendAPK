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
import android.widget.Toast
import com.example.frontendapk.navigation.AppScreens

import androidx.compose.ui.graphics.Color
import com.example.frontendapk.ui.theme.GrayDark
import com.example.frontendapk.data.UserProfileResponse

import java.text.SimpleDateFormat
import java.util.*
import java.time.Instant
import java.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleTicketScreen(navController: NavController, ticketId: Int) {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService

    var detalle by remember { mutableStateOf<DetalleTicket?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isCancelling by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) } // Para mostrar el diálogo de confirmación


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
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
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

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { showDialog = true },
                        enabled = !isCancelling && detalle?.estado == "activo",
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isCancelling && detalle?.estado == "activo") MaterialTheme.colorScheme.primary else GrayDark,
                            contentColor = if (!isCancelling && detalle?.estado == "activo") MaterialTheme.colorScheme.onPrimary else Color.LightGray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isCancelling) "Cancelando..." else "Cancelar Ticket")
                    }
                }
            }
        }
    }







    if (showDialog && detalle != null) {
        val puedeCancelarSinPenalidad = detalle!!.permitir_cancelacion && detalle!!.minutos_restantes_cancelacion > 0
        val minutosRestantes = detalle!!.minutos_restantes_cancelacion

        val mensaje = if (puedeCancelarSinPenalidad) {
            "Este negocio permite cancelar este ticket sin ser penalizado. " +
                    "Tienes $minutosRestantes minutos para realizar esta acción."
        } else {
            "Si cancelas este ticket serás penalizado y tu cuenta será suspendida."
        }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("¿Estás seguro?") },
            text = { Text(mensaje) },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val token = sharedPreferences.getString("ACCESS_TOKEN", null)

                    if (token != null) {
                        isCancelling = true
                        apiService.cancelarTicket(ticketId, "Bearer $token")
                            .enqueue(object : Callback<Map<String, String>> {
                                override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                                    isCancelling = false
                                    if (response.isSuccessful) {
                                        val mensaje = response.body()?.get("mensaje") ?: "Ticket cancelado"
                                        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()

                                        // Verificar estado usuario luego de cancelar
                                        apiService.getUserProfile("Bearer $token")
                                            .enqueue(object : Callback<UserProfileResponse> {
                                                override fun onResponse(call: Call<UserProfileResponse>, perfilResponse: Response<UserProfileResponse>) {
                                                    if (perfilResponse.isSuccessful) {
                                                        val perfil = perfilResponse.body()
                                                        if (perfil?.estado == "suspendido") {
                                                            // Usuario suspendido: limpiar sesión y volver a login
                                                            sharedPreferences.edit().clear().apply()
                                                            navController.navigate(AppScreens.LoginScreen.route) {
                                                                popUpTo(0) { inclusive = true }
                                                            }
                                                        } else {
                                                            // Usuario no suspendido: navegar a pantalla tickets
                                                            navController.navigate(AppScreens.TusTicketsScreen.route) {
                                                                popUpTo(AppScreens.TusTicketsScreen.route) { inclusive = true }
                                                            }
                                                        }
                                                    } else {
                                                        // Error verificando perfil: navegar a pantalla tickets
                                                        navController.navigate(AppScreens.TusTicketsScreen.route) {
                                                            popUpTo(AppScreens.TusTicketsScreen.route) { inclusive = true }
                                                        }
                                                    }
                                                }

                                                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                                                    // Error en llamada perfil: navegar a pantalla tickets
                                                    navController.navigate(AppScreens.TusTicketsScreen.route) {
                                                        popUpTo(AppScreens.TusTicketsScreen.route) { inclusive = true }
                                                    }
                                                }
                                            })

                                    } else {
                                        val errorBody = response.errorBody()?.string()
                                        val mensajeError = try {
                                            val jsonObj = org.json.JSONObject(errorBody ?: "")
                                            jsonObj.optString("error", "Error desconocido")
                                        } catch (e: Exception) {
                                            "Error desconocido"
                                        }
                                        Toast.makeText(context, mensajeError, Toast.LENGTH_LONG).show()
                                    }
                                }

                                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                                    isCancelling = false
                                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
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












    /*
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("¿Estás seguro?") },
            text = { Text("Esta acción cancelará tu ticket y tu cuenta podría ser suspendida.") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val token = sharedPreferences.getString("ACCESS_TOKEN", null)

                    if (token != null) {
                        isCancelling = true
                        apiService.cancelarTicket(ticketId, "Bearer $token")
                            .enqueue(object : Callback<Map<String, String>> {
                                override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                                    isCancelling = false
                                    if (response.isSuccessful) {
                                        val mensaje = response.body()?.get("mensaje") ?: "Ticket cancelado"
                                        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()

                                        // Verificar estado usuario luego de cancelar
                                        apiService.getUserProfile("Bearer $token")
                                            .enqueue(object : Callback<UserProfileResponse> {
                                                override fun onResponse(call: Call<UserProfileResponse>, perfilResponse: Response<UserProfileResponse>) {
                                                    if (perfilResponse.isSuccessful) {
                                                        val perfil = perfilResponse.body()
                                                        if (perfil?.estado == "suspendido") {
                                                            // Usuario suspendido: limpiar sesión y volver a login
                                                            sharedPreferences.edit().clear().apply()
                                                            navController.navigate(AppScreens.LoginScreen.route) {
                                                                popUpTo(0) { inclusive = true }
                                                            }
                                                        } else {
                                                            // Usuario no suspendido: navegar a pantalla tickets
                                                            navController.navigate(AppScreens.TusTicketsScreen.route) {
                                                                popUpTo(AppScreens.TusTicketsScreen.route) { inclusive = true }
                                                            }
                                                        }
                                                    } else {
                                                        // Error verificando perfil: navegar a pantalla tickets
                                                        navController.navigate(AppScreens.TusTicketsScreen.route) {
                                                            popUpTo(AppScreens.TusTicketsScreen.route) { inclusive = true }
                                                        }
                                                    }
                                                }

                                                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                                                    // Error en llamada perfil: navegar a pantalla tickets
                                                    navController.navigate(AppScreens.TusTicketsScreen.route) {
                                                        popUpTo(AppScreens.TusTicketsScreen.route) { inclusive = true }
                                                    }
                                                }
                                            })

                                    } else {
                                        val errorBody = response.errorBody()?.string()
                                        val mensajeError = try {
                                            val jsonObj = org.json.JSONObject(errorBody ?: "")
                                            jsonObj.optString("error", "Error desconocido")
                                        } catch (e: Exception) {
                                            "Error desconocido"
                                        }
                                        Toast.makeText(context, mensajeError, Toast.LENGTH_LONG).show()
                                    }
                                }

                                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                                    isCancelling = false
                                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
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
    }*/
}