package com.example.tutoring.ui.navigation.tutor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorMainScreen(onLoginOut: () -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        topBar = {
            val titleText = when {
                currentRoute?.startsWith("tutor_home") == true -> "Home-Tutor"
                currentRoute?.startsWith("tutor_application") == true -> "Application"
                currentRoute?.startsWith("tutor_courses") == true -> "Courses"
                currentRoute?.startsWith("tutor_profile") == true -> "Profile"
                currentRoute?.startsWith("tutor_lessons") == true -> "Lessons"
                else -> "Tutor"
            }

            TopAppBar(
                title = { Text(titleText) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFFEBEBF6),
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    if (currentRoute?.startsWith("tutor_lessons") == true || currentRoute?.startsWith("tutor_add_lesson") == true) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            TutorBottomBar(navController)
        }
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            TutorNavHost(navController,onLoginOut)
        }
    }
}
