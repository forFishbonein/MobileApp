package com.example.tutoring.ui.navigation.student

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentMainScreen() {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("学生端") }
            )
        },
        bottomBar = {
            StudentBottomBar(navController)
        }
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            StudentNavHost(navController)
        }
    }
}
