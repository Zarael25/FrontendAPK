package com.example.frontendapk.view

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapk.data.User  // Importación de la clase User
import com.example.frontendapk.data.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.VisualTransformation
import androidx.navigation.NavController  // Importar NavController

@Composable
fun RegisterScreen(navController: NavController, modifier: Modifier = Modifier) {
    // Estados para los campos de entrada
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }

    // Obtener el contexto
    val context = LocalContext.current

    // Coloca la UI aquí
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp) // padding general
            .then(Modifier.fillMaxWidth()) // Se combinan los modificadores
    ) {
        Text(text = "Registro de Usuario", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = username,
            onValueChange = { username = it },
            label = "Nombre de usuario",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = "Nombre completo",
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomTextField(
            value = correo,
            onValueChange = { correo = it },
            label = "Correo electrónico",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomTextField(
            value = password,
            onValueChange = { password = it },
            label = "Contraseña",
            isPassword = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {

                if (username.isBlank() || password.isBlank() || nombre.isBlank() || correo.isBlank()) {
                    Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()

                    Log.d("BOTON_REGISTRO", "Se presionó el botón de registrar pero esta vacio")
                } else {
                    val user = User(username, password, nombre, correo)
                    RetrofitClient.apiService.registerUser(user).enqueue(object : Callback<User> {
                        override fun onResponse(call: Call<User>, response: Response<User>) {
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error en el registro", Toast.LENGTH_SHORT).show()
                                Log.d("REGISTRO", "Código HTTP: ${response.code()} - Error: ${response.errorBody()?.string()}")
                            }
                        }

                        override fun onFailure(call: Call<User>, t: Throwable) {
                            Toast.makeText(context, "Fallo la conexión", Toast.LENGTH_SHORT).show()
                            Log.e("REGISTRO", "Error en la conexión", t)
                        }
                    })
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para regresar a LoginScreen
        Button(
            onClick = {
                navController.popBackStack()  // Regresar a la pantalla anterior (LoginScreen)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al Login")
        }
    }
}

@Composable
fun CustomTextField(
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
fun PreviewRegisterScreen() {
    RegisterScreen(navController = NavController(LocalContext.current)) // Reemplazar con un NavController válido
}