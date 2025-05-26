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
import com.example.frontendapk.data.NegocioRequest
import com.example.frontendapk.data.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroNegocioScreen(navController: NavController) {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService

    var nombre by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var numReferencia by remember { mutableStateOf("") }
    var detalle by remember { mutableStateOf("") }

    // Dropdown estado para categoría
    val categorias = listOf("Salud", "Financiera", "Educación", "Tecnología", "Restaurantes", "Otros")
    val categoriaMap = mapOf(
        "Salud" to "salud",
        "Financiera" to "financiera",
        "Educación" to "educacion",
        "Tecnología" to "tecnologia",
        "Restaurantes" to "restaurantes",
        "Otros" to "otros"
    )

    var expanded by remember { mutableStateOf(false) }
    var categoriaSeleccionada by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Negocio") },
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
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección") }
            )

            // Dropdown para Categoría
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {

                OutlinedTextField(
                    value = categoriaSeleccionada,
                    onValueChange = { /* no editable */ },
                    label = { Text("Categoría") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )


                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categorias.forEach { categoria ->
                        DropdownMenuItem(
                            onClick = {
                                categoriaSeleccionada = categoria
                                expanded = false
                            },
                            text = { Text(categoria) }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = numReferencia,
                onValueChange = { numReferencia = it },
                label = { Text("Número de Referencia") }
            )
            OutlinedTextField(
                value = detalle,
                onValueChange = { detalle = it },
                label = { Text("Detalle") },
                modifier = Modifier.height(100.dp)
            )

            Button(
                onClick = {
                    if (categoriaSeleccionada.isEmpty()) {
                        Toast.makeText(context, "Seleccione una categoría", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val token = prefs.getString("ACCESS_TOKEN", null)

                    if (!token.isNullOrEmpty()) {
                        val negocio = NegocioRequest(
                            nombre = nombre,
                            direccion = direccion,
                            categoria = categoriaMap[categoriaSeleccionada] ?: "",
                            num_referencia = numReferencia,
                            detalle = detalle
                        )

                        apiService.crearNegocio("Bearer $token", negocio)
                            .enqueue(object : Callback<com.example.frontendapk.data.Negocio> {
                                override fun onResponse(
                                    call: Call<com.example.frontendapk.data.Negocio>,
                                    response: Response<com.example.frontendapk.data.Negocio>
                                ) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "Negocio registrado correctamente", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack() // Volver atrás
                                    } else {
                                        val errorBody = response.errorBody()?.string()
                                        if (!errorBody.isNullOrEmpty()) {
                                            try {
                                                val jsonError = org.json.JSONObject(errorBody)
                                                val detailMessage = jsonError.optString("detail")
                                                if (detailMessage.isNotEmpty()) {
                                                    Toast.makeText(context, detailMessage, Toast.LENGTH_LONG).show()
                                                } else {
                                                    Toast.makeText(context, "Error al registrar el negocio", Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Error al procesar el error del servidor", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            Toast.makeText(context, "Error al registrar el negocio", Toast.LENGTH_SHORT).show()
                                        }
                                        Log.e("RegistroNegocio", "Error HTTP: ${response.code()} - $errorBody")
                                    }
                                }

                                override fun onFailure(call: Call<com.example.frontendapk.data.Negocio>, t: Throwable) {
                                    Log.e("RegistroNegocio", "Fallo en Retrofit: ${t.message}")
                                    Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
                                }
                            })
                    } else {
                        Toast.makeText(context, "Token no válido", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar")
            }
        }
    }
}