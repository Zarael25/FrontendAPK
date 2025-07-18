package com.example.frontendapk.view
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarUsuarioAdminScreen(navController: NavController, usuarioId: Int) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var estadoActivo by remember { mutableStateOf(true) }  // true = activo, false = suspendido
    var suscripcionPro by remember { mutableStateOf(true) }  // true = pro, false = free
    var password by remember { mutableStateOf("") }

    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val apiService = RetrofitClient.apiService
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("ACCESS_TOKEN", null) ?: ""

    var showConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(usuarioId) {
        apiService.getUsuarioById("Bearer $token", usuarioId).enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                if (response.isSuccessful) {
                    response.body()?.let { usuario ->
                        estadoActivo = usuario.estado == "activo"
                        suscripcionPro = usuario.suscripcion == "pro"
                    }
                } else {
                    errorMessage = "Error al obtener datos del usuario: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                errorMessage = "Error de red: ${t.message}"
            }
        })
    }


    fun guardarCambios() {
        isSaving = true
        errorMessage = null
        successMessage = null

        // Prepara el body para PATCH solo con campos a editar
        val patchData = mutableMapOf<String, Any>()
        patchData["estado"] = if (estadoActivo) "activo" else "suspendido"
        patchData["suscripcion"] = if (suscripcionPro) "pro" else "free"
        if (password.isNotBlank()) {
            patchData["password"] = password
        }

        // Llama al endpoint PATCH
        apiService.adminEditarUsuario(token = "Bearer $token", idUsuario = usuarioId, body = patchData)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    isSaving = false
                    if (response.isSuccessful) {
                        successMessage = "Usuario actualizado correctamente"
                        // Opcional: volver atrás
                        // navController.popBackStack()
                    } else {
                        errorMessage = "Error al actualizar: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    isSaving = false
                    errorMessage = "Error de red: ${t.message}"
                }
            })
    }

    Scaffold(

        topBar = {
            TopAppBar(
                title = { Text("Editar Usuario") },
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
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Estado")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = estadoActivo,
                        onCheckedChange = { estadoActivo = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (estadoActivo) "Activo" else "Suspendido")
                }
            }

            // Suscripción
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Suscripción")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = suscripcionPro,
                        onCheckedChange = { suscripcionPro = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (suscripcionPro) "Pro" else "Free")
                }
            }

            // Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Nueva contraseña (dejar vacío para no cambiar)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            // Mensajes de error o éxito
            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            successMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.primary)
            }

            // Botón guardar
            Button(
                onClick = { showConfirmDialog = true },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Guardar cambios")
                }
            }
        }

        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("Confirmar cambios") },
                text = { Text("¿Estás seguro de que deseas guardar los cambios?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showConfirmDialog = false
                            guardarCambios()
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }


    }
}