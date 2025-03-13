package com.example.tutoring.ui.navigation.auth

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.tutoring.data.Role

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthMainScreen(onLoginSuccess: (Role) -> Unit,context: Context) {
    // The NavController is only responsible for switching between login/registration
    val authNavController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Login / Registration") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            AuthNavHost(authNavController, onLoginSuccess, context)
        }
    }
}
