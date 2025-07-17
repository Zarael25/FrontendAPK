package com.example.frontendapk.view
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.frontendapk.data.LoginRequest
import com.example.frontendapk.data.AdminLoginResponse
import com.example.frontendapk.data.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.frontendapk.navigation.AppScreens
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginAdminScreen(navController: NavController) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),

            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    modifier = Modifier
                        .clickable { navController.popBackStack() }
                        .padding(end = 8.dp)
                )
                Text(
                    text = "Iniciar Sesión Admin",
                    style = MaterialTheme.typography.headlineLarge
                )
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        LoginCustomTextField(
            value = username,
            onValueChange = { username = it },
            label = "Nombre de usuario admin",
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
                if (username.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                } else {
                    val loginRequest = LoginRequest(username, password)
                    RetrofitClient.apiService.loginAdmin(loginRequest).enqueue(object : Callback<AdminLoginResponse> {
                        override fun onResponse(call: Call<AdminLoginResponse>, response: Response<AdminLoginResponse>) {
                            if (response.isSuccessful) {
                                val data = response.body()
                                if (data != null) {
                                    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                    prefs.edit()
                                        .putString("ACCESS_TOKEN", data.access)
                                        .putString("REFRESH_TOKEN", data.refresh)
                                        .putInt("USER_ID", data.usuarioId)
                                        .putString("NOMBRE", data.nombre)
                                        .putString("SUSCRIPCION", data.suscripcion)
                                        .putString("TIPO_USUARIO", data.tipoUsuario)
                                        .apply()

                                    Toast.makeText(context, "Bienvenido admin ${data.nombre}", Toast.LENGTH_SHORT).show()

                                    navController.navigate(AppScreens.HomeAdminScreen.route) {
                                        popUpTo(AppScreens.LoginAdminScreen.route) { inclusive = true }
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                                Log.d("LOGIN_ADMIN", "Error: ${response.errorBody()?.string()}")
                            }
                        }

                        override fun onFailure(call: Call<AdminLoginResponse>, t: Throwable) {
                            Toast.makeText(context, "Error en la conexión", Toast.LENGTH_SHORT).show()
                            Log.e("LOGIN_ADMIN", "Fallo conexión", t)
                        }
                    })
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }
    }
}