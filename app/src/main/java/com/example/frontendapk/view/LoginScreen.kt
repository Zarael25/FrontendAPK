package com.example.frontendapk.view

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.frontendapk.data.LoginRequest
import com.example.frontendapk.data.LoginResponse
import com.example.frontendapk.data.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.foundation.clickable

@Composable
fun LoginScreen(navController: NavController) {
    // Estados para los campos de entrada
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    // Obtener el contexto
    val context = LocalContext.current

    // UI del Login
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Iniciar Sesión", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(16.dp))

        LoginCustomTextField(
            value = username,
            onValueChange = { username = it },
            label = "Nombre de usuario",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        LoginCustomTextField(
            value = password,
            onValueChange = { password = it },
            label = "Contraseña",
            isPassword = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Validación de campos vacíos
                if (username.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                } else {
                    val loginRequest = LoginRequest(username, password)
                    RetrofitClient.apiService.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
                        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                            if (response.isSuccessful) {
                                // Obtener el token y almacenarlo
                                Log.d("LOGIN", "Response token: ${response.body()?.accessToken}")
                                val accessToken = response.body()?.accessToken ?: ""
                                val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                sharedPreferences.edit()
                                    .putString("ACCESS_TOKEN", accessToken)
                                    .putString("USERNAME", username)
                                    .apply()
                                Log.d("LOGIN", "Token guardado: $accessToken")
                                Log.d("LOGIN", "Username guardado: $username")



                                // Mostrar mensaje de éxito
                                Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

                                // Navegar a la pantalla principal
                                navController.navigate("home_screen/${username}") {
                                    // Evitar regresar a la pantalla de login al presionar atrás
                                    popUpTo("login_screen") { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                                Log.d("LOGIN", "Código HTTP: ${response.code()} - Error: ${response.errorBody()?.string()}")
                            }
                        }

                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            Toast.makeText(context, "Fallo la conexión", Toast.LENGTH_SHORT).show()
                            Log.e("LOGIN", "Error en la conexión", t)
                        }
                    })
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Texto para navegar al registro
        Text(
            text = "¿No tienes cuenta? Regístrate aquí",
            style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold),
            modifier = Modifier.clickable {
                // Navegar a RegisterScreen
                navController.navigate("register_screen")
            }
        )
    }
}

@Composable
fun LoginCustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen(navController = NavController(LocalContext.current)) // Reemplazar con un NavController válido
}