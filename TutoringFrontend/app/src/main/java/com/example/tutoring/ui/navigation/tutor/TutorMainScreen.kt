package com.example.tutoring.ui.navigation.tutor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorMainScreen() {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("导师端") }
            )
        },
        bottomBar = {
            TutorBottomBar(navController)
        }
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            TutorNavHost(navController)
        }
    }
}
