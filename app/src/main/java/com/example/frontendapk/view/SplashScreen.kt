package com.example.frontendapk.view

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.frontendapk.R
import kotlinx.coroutines.delay

val KanitFont = FontFamily(
    Font(R.font.kanit_regular, FontWeight.Normal)
)

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(3000) // espera 3 segundos

        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        val username = sharedPreferences.getString("USERNAME", null)

        Log.d("SPLASH", "Token leído: $accessToken")
        Log.d("SPLASH", "Username leído: $username")

        if (!accessToken.isNullOrEmpty() && !username.isNullOrEmpty()) {
            navController.navigate("home_screen/$username") {
                popUpTo("splash_screen") { inclusive = true }
            }
        } else {
            navController.navigate("login_screen") {
                popUpTo("splash_screen") { inclusive = true }
            }
        }
    }

    Splash()
}

@Composable
fun Splash() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo4),
            contentDescription = "logo",
            Modifier.size(200.dp, 200.dp)
        )

        Text(
            "Filas Virtuales",
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = KanitFont
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    Splash()
}