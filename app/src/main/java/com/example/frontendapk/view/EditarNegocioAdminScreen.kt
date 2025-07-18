package com.example.frontendapk.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.example.frontendapk.data.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.Alignment
import com.example.frontendapk.data.Usuario
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import android.widget.Toast
import android.util.Log
import androidx.compose.ui.text.style.TextAlign
import com.example.frontendapk.data.Negocio
import android.content.Intent
import android.net.Uri


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarNegocioAdminScreen(
    navController: NavController,
    negocioId: Int
) {
    val estados = listOf("en_revision", "verificado", "rechazado", "oculto")
    var nombreNegocio by remember { mutableStateOf<String?>(null) }
    var estadoSeleccionado by remember { mutableStateOf<String?>(null) }
    var docRespaldoUrl by remember { mutableStateOf<String?>(null) }

    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val token = prefs.getString("ACCESS_TOKEN", null)

    LaunchedEffect(negocioId, token) {
        if (token != null) {
            val negocio = obtenerNegocio(negocioId, token)
            if (negocio != null) {
                nombreNegocio = negocio.nombre
                docRespaldoUrl = negocio.doc_respaldo
                if (negocio.estado != null && estados.contains(negocio.estado)) {
                    estadoSeleccionado = negocio.estado
                } else {
                    estadoSeleccionado = estados[0]
                }
            } else {
                estadoSeleccionado = estados[0]
            }
        } else {
            Toast.makeText(context, "Token no encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Estado del Negocio") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            nombreNegocio?.let { nombre ->
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = estadoSeleccionado ?: "",  // Si es null, mostramos cadena vacía
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Seleccionar estado") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    estados.forEach { estado ->
                        DropdownMenuItem(
                            text = { Text(estado) },
                            onClick = {
                                estadoSeleccionado = estado
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            docRespaldoUrl?.let { url ->
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                    
                ) {
                    Text("Descargar documento de respaldo")
                }
            }



            Spacer(modifier = Modifier.height(24.dp))

            Button(
                enabled = estadoSeleccionado != null,  // Solo activo si ya cargó estado
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar cambios")
            }
        }

        if (showDialog && estadoSeleccionado != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Confirmar") },
                text = { Text("¿Deseas cambiar el estado a '$estadoSeleccionado'?") },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        cambiarEstadoNegocio(negocioId, estadoSeleccionado!!, navController, context)
                    }) {
                        Text("Sí, confirmar")
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
}

fun cambiarEstadoNegocio(
    id: Int,
    nuevoEstado: String,
    navController: NavController,
    context: Context
) {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val token = prefs.getString("ACCESS_TOKEN", null)

    if (token == null) {
        Toast.makeText(context, "Token no encontrado", Toast.LENGTH_SHORT).show()
        return
    }

    val call = RetrofitClient.apiService.cambiarEstadoNegocio(
        "Bearer $token",
        id,
        mapOf("estado" to nuevoEstado)
    )

    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                Toast.makeText(context, "Estado actualizado correctamente", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            } else {
                Toast.makeText(context, "Error al actualizar estado", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Toast.makeText(context, "Fallo de red: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}

suspend fun obtenerNegocio(negocioId: Int, token: String): Negocio? {
    return try {
        val response = RetrofitClient.apiService.obtenerNegocioPorId("Bearer $token", negocioId)
        if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    } catch (e: Exception) {
        Log.e("EditarNegocioScreen", "Excepción al obtener negocio", e)
        null
    }
}