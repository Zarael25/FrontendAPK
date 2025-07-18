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
import com.example.frontendapk.data.FilaAtencion
import com.example.frontendapk.data.RetrofitClient
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
fun EditarFilaScreen(navController: NavController, filaId: Int) {
    val context = LocalContext.current



    fun tiempoAHorasMinutos(tiempo: String): Int {
        val partes = tiempo.split(":")
        val horas = partes.getOrNull(0)?.toIntOrNull() ?: 0
        val minutos = partes.getOrNull(1)?.toIntOrNull() ?: 0
        return horas * 60 + minutos
    }

    fun minutosAHHMMSS(minutos: Int): String {
        val horas = minutos / 60
        val minutosRestantes = minutos % 60
        return String.format("%02d:%02d:00", horas, minutosRestantes)
    }





    val apiService = RetrofitClient.apiService

    var nombre by remember { mutableStateOf("") }
    var cantidadTickets by remember { mutableStateOf("") }
    var periodoAtencion by remember { mutableStateOf("") }
    var minutosTexto by remember { mutableStateOf("") }
    var periodoActivado by remember { mutableStateOf(false) }
    var apertura by remember { mutableStateOf("") }
    var finalizacion by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(true) }
    var permitirCancelacion by remember { mutableStateOf(true) }

    // Calendars para TimePickerDialog
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

    // Carga inicial de datos
    LaunchedEffect(filaId) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("ACCESS_TOKEN", null)

        if (!token.isNullOrEmpty()) {
            apiService.getFilaPorId("Bearer $token", filaId).enqueue(object : Callback<com.example.frontendapk.data.FilaAtencion> {
                override fun onResponse(call: Call<com.example.frontendapk.data.FilaAtencion>, response: Response<com.example.frontendapk.data.FilaAtencion>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            nombre = it.nombre ?: ""
                            cantidadTickets = it.cantidad_tickets.toString()

                            // Si periodoAtencion es "00:00:00" o vacío, desactiva input y limpia
                            if (it.periodo_atencion == null || it.periodo_atencion == "00:00:00" || it.periodo_atencion.isEmpty()) {
                                periodoActivado = false
                                periodoAtencion = ""
                            } else {
                                periodoActivado = true
                                periodoAtencion = it.periodo_atencion ?: ""
                                minutosTexto = if (periodoAtencion.isNotEmpty()) tiempoAHorasMinutos(periodoAtencion).toString() else ""
                            }

                            apertura = it.apertura ?: ""
                            finalizacion = it.finalizacion ?: ""
                        }
                        cargando = false
                    } else {
                        Toast.makeText(context, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                        cargando = false
                    }
                }

                override fun onFailure(call: Call<com.example.frontendapk.data.FilaAtencion>, t: Throwable) {
                    Log.e("EditarFila", "Error: ${t.message}")
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
                title = { Text("Editar Fila") },
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
                label = { Text("Nombre") }
            )
            OutlinedTextField(
                value = cantidadTickets,
                onValueChange = { cantidadTickets = it.filter { c -> c.isDigit() } },
                label = { Text("Cantidad de Tickets") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Row con input + switch para periodoAtencion
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = minutosTexto,
                    onValueChange = {
                        if (it.all { c -> c.isDigit() }) {
                            minutosTexto = it
                            periodoAtencion = minutosAHHMMSS(it.toIntOrNull() ?: 0)
                        }
                    },
                    label = { Text("Periodo de Atención (min)") },
                    modifier = Modifier.weight(7f),
                    enabled = periodoActivado,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Column(
                    modifier = Modifier
                        .weight(3f)
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Activar", style = MaterialTheme.typography.labelMedium)
                    Switch(
                        checked = periodoActivado,
                        onCheckedChange = {
                            periodoActivado = it
                            if (!it) {
                                periodoAtencion = ""
                                minutosTexto = ""
                            }
                        }
                    )
                }
            }

            // Hora de apertura (readOnly + TimePicker)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { aperturaTimePickerDialog.show() }
            ) {
                OutlinedTextField(
                    value = apertura.ifEmpty { "00:00:00" },
                    onValueChange = {},
                    label = { Text("Hora de Apertura (HH:mm:ss)") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.Schedule, contentDescription = "Seleccionar hora")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledBorderColor = Color.Gray
                    )
                )
            }

            // Hora de finalización (readOnly + TimePicker)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { finalizacionTimePickerDialog.show() }
            ) {
                OutlinedTextField(
                    value = finalizacion.ifEmpty { "00:00:00" },
                    onValueChange = {},
                    label = { Text("Hora de Finalización (HH:mm:ss)") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.Schedule, contentDescription = "Seleccionar hora")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledBorderColor = Color.Gray
                    )
                )
            }

            // Botón para guardar cambios
            Button(
                onClick = {
                    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val token = prefs.getString("ACCESS_TOKEN", null)

                    if (!token.isNullOrEmpty()) {
                        val datos = mutableMapOf<String, Any>()

                        datos["nombre"] = nombre
                        datos["cantidad_tickets"] = cantidadTickets.toIntOrNull() ?: 0
                        // Solo mandar periodo si está activado, sino 00:00:00
                        datos["periodo_atencion"] = if (periodoActivado) periodoAtencion else "00:00:00"
                        datos["apertura"] = apertura
                        datos["finalizacion"] = finalizacion
                        datos["permitir_cancelacion"] = permitirCancelacion

                        apiService.editarFilaParcial("Bearer $token", filaId, datos)
                            .enqueue(object : Callback<Void> {
                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "Fila actualizada correctamente", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    } else {
                                        Log.e("EditarFila", "Error HTTP: ${response.code()}")
                                        Toast.makeText(context, "Error al actualizar la fila", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    Log.e("EditarFila", "Fallo en Retrofit: ${t.message}")
                                    Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
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




/*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarFilaScreen(navController: NavController, filaId: Int) {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService

    var nombre by remember { mutableStateOf("") }
    var cantidadTickets by remember { mutableStateOf("") }
    var periodoAtencion by remember { mutableStateOf("") }
    var apertura by remember { mutableStateOf("") }
    var finalizacion by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(true) }
    var permitirCancelacion by remember { mutableStateOf(true) }

    // Obtener datos actuales
    LaunchedEffect(filaId) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("ACCESS_TOKEN", null)

        if (!token.isNullOrEmpty()) {
            apiService.getFilaPorId("Bearer $token", filaId).enqueue(object : Callback<com.example.frontendapk.data.FilaAtencion> {
                override fun onResponse(call: Call<com.example.frontendapk.data.FilaAtencion>, response: Response<com.example.frontendapk.data.FilaAtencion>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            nombre = it.nombre ?: ""
                            cantidadTickets = it.cantidad_tickets.toString()
                            periodoAtencion = it.periodo_atencion ?: ""
                            apertura = it.apertura ?: ""
                            finalizacion = it.finalizacion ?: ""

                        }
                        cargando = false
                    } else {
                        Toast.makeText(context, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                        cargando = false
                    }
                }

                override fun onFailure(call: Call<com.example.frontendapk.data.FilaAtencion>, t: Throwable) {
                    Log.e("EditarFila", "Error: ${t.message}")
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
                title = { Text("Editar Fila") },
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
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
            OutlinedTextField(value = cantidadTickets, onValueChange = { cantidadTickets = it.filter { c -> c.isDigit() } }, label = { Text("Cantidad de Tickets") })
            OutlinedTextField(value = periodoAtencion, onValueChange = { periodoAtencion = it }, label = { Text("Periodo de Atención (HH:mm:ss)") })
            OutlinedTextField(value = apertura, onValueChange = { apertura = it }, label = { Text("Hora de Apertura (HH:mm:ss)") })
            OutlinedTextField(value = finalizacion, onValueChange = { finalizacion = it }, label = { Text("Hora de Finalización (HH:mm:ss)") })


            Button(
                onClick = {
                    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val token = prefs.getString("ACCESS_TOKEN", null)

                    if (!token.isNullOrEmpty()) {
                        val datos = mutableMapOf<String, Any>()

                        datos["nombre"] = nombre
                        datos["cantidad_tickets"] = cantidadTickets.toIntOrNull() ?: 0
                        datos["periodo_atencion"] = periodoAtencion
                        datos["apertura"] = apertura
                        datos["finalizacion"] = finalizacion
                        datos["permitir_cancelacion"] = permitirCancelacion

                        apiService.editarFilaParcial("Bearer $token", filaId, datos)
                            .enqueue(object : Callback<Void> {
                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "Fila actualizada correctamente", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    } else {
                                        Log.e("EditarFila", "Error HTTP: ${response.code()}")
                                        Toast.makeText(context, "Error al actualizar la fila", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    Log.e("EditarFila", "Fallo en Retrofit: ${t.message}")
                                    Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
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
}*/