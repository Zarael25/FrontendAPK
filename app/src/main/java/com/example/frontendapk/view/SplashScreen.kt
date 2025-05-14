package com.example.frontendapk.view


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.frontendapk.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController


import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily

val KanitFont = FontFamily(
    Font(R.font.kanit_regular, FontWeight.Normal)
)

@Composable
fun SplashScreen(navController: NavController) {
    // Navegar automáticamente después de 2 segundos
    LaunchedEffect(Unit) {
        Handler(Looper.getMainLooper()).postDelayed({
            navController.navigate("login_screen") {
                popUpTo("splash_screen") { inclusive = true }
            }
        }, 5000)
    }


    // Contenido del splash
    Splash()
}

@Composable
fun Splash() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Aquí puedes añadir lo que quieras, como texto o imagen
        Image(
            painter = painterResource(id = R.drawable.logo3),
            contentDescription = "logo",
            Modifier.size(200.dp,200.dp)
        )

        Text(
            "Fichas Virtuales",
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