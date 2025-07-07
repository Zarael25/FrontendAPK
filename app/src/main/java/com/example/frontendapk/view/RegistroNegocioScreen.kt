package com.example.frontendapk.view

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroNegocioScreen(navController: NavController) {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService

    var nombre by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var numReferencia by remember { mutableStateOf("") }
    var detalle by remember { mutableStateOf("") }

    // Manejo del documento
    var docUri by remember { mutableStateOf<Uri?>(null) }
    var docNombre by remember { mutableStateOf("") }

    val docPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        docUri = uri
        uri?.let {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    docNombre = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
    }

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
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
            OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") })

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = categoriaSeleccionada,
                    onValueChange = {},
                    label = { Text("Categoría") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    categorias.forEach { categoria ->
                        DropdownMenuItem(onClick = {
                            categoriaSeleccionada = categoria
                            expanded = false
                        }, text = { Text(categoria) })
                    }
                }
            }

            OutlinedTextField(value = numReferencia, onValueChange = { numReferencia = it }, label = { Text("Número de Referencia") })
            OutlinedTextField(value = detalle, onValueChange = { detalle = it }, label = { Text("Detalle") }, modifier = Modifier.height(100.dp))

            Button(onClick = { docPickerLauncher.launch("application/pdf") }) {
                Text(if (docNombre.isNotEmpty()) "Documento: $docNombre" else "Seleccionar Documento PDF")
            }

            Button(
                onClick = {
                    if (categoriaSeleccionada.isEmpty()) {
                        Toast.makeText(context, "Seleccione una categoría", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val token = prefs.getString("ACCESS_TOKEN", null)

                    if (!token.isNullOrEmpty()) {
                        val requestMap = mutableMapOf<String, okhttp3.RequestBody>()

                        requestMap["nombre"] = nombre.toRequestBody()
                        requestMap["direccion"] = direccion.toRequestBody()
                        requestMap["categoria"] = (categoriaMap[categoriaSeleccionada] ?: "").toRequestBody()
                        requestMap["num_referencia"] = numReferencia.toRequestBody()
                        requestMap["detalle"] = detalle.toRequestBody()

                        val filePart = docUri?.let { uri ->
                            val inputStream = context.contentResolver.openInputStream(uri)
                            val tempFile = File.createTempFile("doc_respaldo", ".pdf", context.cacheDir)
                            val outputStream = FileOutputStream(tempFile)
                            inputStream?.copyTo(outputStream)
                            outputStream.close()

                            val requestFile = tempFile.asRequestBody("application/pdf".toMediaTypeOrNull())
                            MultipartBody.Part.createFormData("doc_respaldo", docNombre, requestFile)
                        }

                        RetrofitClient.apiService.crearNegocioMultipart("Bearer $token", requestMap, filePart)
                            .enqueue(object : Callback<com.example.frontendapk.data.Negocio> {
                                override fun onResponse(call: Call<com.example.frontendapk.data.Negocio>, response: Response<com.example.frontendapk.data.Negocio>) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "Negocio registrado correctamente", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    } else {
                                        val errorBody = response.errorBody()?.string()
                                        Toast.makeText(context, "Error: $errorBody", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<com.example.frontendapk.data.Negocio>, t: Throwable) {
                                    Toast.makeText(context, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar")
            }
        }
    }
}

// Helper para RequestBody
fun String.toRequestBody() =
    okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), this)