package com.example.frontendapk.view

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.frontendapk.data.RetrofitClient
import com.example.frontendapk.data.FilaRequest // Asegúrate de tener esta clase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import android.app.TimePickerDialog
import java.util.Calendar
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.ui.Alignment
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroFilaScreen(navController: NavController, negocioId: Int) {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService

    var nombre by remember { mutableStateOf("") }
    var cantidadTickets by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(true) }
    var permitirCancelacion by remember { mutableStateOf(true) }

    // Ahora periodoAtencion es un número (minutos)
    var periodoAtencionMinutos by remember { mutableStateOf("") }

    // Para apertura y finalizacion guardamos un LocalTime o un String
    var apertura by remember { mutableStateOf("") }
    var finalizacion by remember { mutableStateOf("") }

    // Para mostrar los time pickers
    val aperturaCalendar = Calendar.getInstance()
    val finalizacionCalendar = Calendar.getInstance()

    val aperturaTimePickerDialog = TimePickerDialog(
        context,
        { _, hour: Int, minute: Int ->
            apertura = String.format("%02d:%02d:00", hour, minute)
        },
        aperturaCalendar.get(Calendar.HOUR_OF_DAY),
        aperturaCalendar.get(Calendar.MINUTE),
        true
    )

    val finalizacionTimePickerDialog = TimePickerDialog(
        context,
        { _, hour: Int, minute: Int ->
            finalizacion = String.format("%02d:%02d:00", hour, minute)
        },
        finalizacionCalendar.get(Calendar.HOUR_OF_DAY),
        finalizacionCalendar.get(Calendar.MINUTE),
        true
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Fila de Atención") },
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
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre de la fila")},
            )
            OutlinedTextField(
                value = cantidadTickets,
                onValueChange = {
                    // Solo números positivos
                    if (it.all { c -> c.isDigit() }) cantidadTickets = it
                },
                label = { Text("Cantidad de tickets") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )




            var periodoAtencionMinutos by remember { mutableStateOf("") }
            var periodoActivado by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Input para periodo de atención
                OutlinedTextField(
                    value = periodoAtencionMinutos,
                    onValueChange = {
                        if (it.all { c -> c.isDigit() }) periodoAtencionMinutos = it
                    },
                    label = { Text("Periodo de atención (min)") },
                    modifier = Modifier.weight(7f),
                    enabled = periodoActivado,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )

                // Switch Activar
                Column(
                    modifier = Modifier.weight(3f), // igual peso para que ocupe la mitad
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Activar", style = MaterialTheme.typography.labelMedium)
                    Switch(
                        checked = periodoActivado,
                        onCheckedChange = {
                            periodoActivado = it
                            if (!it) periodoAtencionMinutos = "" // Limpiar si se desactiva
                        }
                    )
                }
            }



            // Campo de hora de apertura

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { aperturaTimePickerDialog.show() }
            ) {
                OutlinedTextField(
                    value = apertura.ifEmpty { "00:00:00" }, // Mostrar algo por defecto
                    onValueChange = {},
                    label = { Text("Hora de apertura (hh:mm:ss)") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.Schedule, contentDescription = "Seleccionar hora")
                    },
                    modifier = Modifier.fillMaxWidth(0.7f),
                    enabled = false, // evita edición y activa correctamente el click en el padre
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledBorderColor = Color.Gray
                    )
                )
            }


            // Campo de hora de finalización
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { finalizacionTimePickerDialog.show() }
            ) {
                OutlinedTextField(
                    value = finalizacion.ifEmpty { "00:00:00" },
                    onValueChange = {},
                    label = { Text("Hora de finalización (hh:mm:ss)") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.Schedule, contentDescription = "Seleccionar hora")
                    },
                    modifier = Modifier.fillMaxWidth(0.7f),
                    enabled = false,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledBorderColor = Color.Gray
                    )
                )
            }

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("Visible:")
                Spacer(Modifier.width(8.dp))
                Switch(
                    checked = visible,
                    onCheckedChange = { visible = it }
                )
            }

            Button(
                onClick = {
                    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val token = prefs.getString("ACCESS_TOKEN", null)

                    if (!token.isNullOrEmpty()) {
                        // Convertir minutos a hh:mm:ss
                        val min = periodoAtencionMinutos.toIntOrNull() ?: 0
                        val horas = min / 60
                        val minutos = min % 60
                        val periodoFormateado = String.format("%02d:%02d:00", horas, minutos)

                        val fila = FilaRequest(
                            nombre = nombre,
                            cantidad_tickets = cantidadTickets.toIntOrNull() ?: 0,
                            visible = visible,
                            periodo_atencion = periodoFormateado,
                            apertura = apertura,
                            finalizacion = finalizacion,
                            negocio = negocioId,
                            numero_ticket_actual = 0
                        )

                        apiService.crearFila("Bearer $token", fila)
                            .enqueue(object : Callback<Unit> {
                                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "Fila registrada correctamente", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    } else {
                                        val errorBody = response.errorBody()?.string()
                                        try {
                                            val errorJson = org.json.JSONObject(errorBody)
                                            val detailMessage = errorJson.getString("detail")
                                            Toast.makeText(context, detailMessage, Toast.LENGTH_LONG).show()
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Error inesperado", Toast.LENGTH_LONG).show()
                                        }
                                        Log.e("RegistroFila", "Error HTTP: ${response.code()} - $errorBody")
                                    }
                                }

                                override fun onFailure(call: Call<Unit>, t: Throwable) {
                                    Log.e("RegistroFila", "Fallo en Retrofit: ${t.message}")
                                    Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
                                }
                            })
                    } else {
                        Toast.makeText(context, "Token no válido", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar Fila")
            }
        }
    }
}