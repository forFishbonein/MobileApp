package com.example.tutoring.ui.navigation.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tutoring.data.Role
import com.example.tutoring.utils.LoadingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentMainScreen(
    onLoginOut: () -> Unit,
    loadingViewModel: LoadingViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        topBar = {
            // The title is determined based on the current route
            val titleText = when {
                currentRoute?.startsWith("student_home") == true -> "Home-Student"
                currentRoute?.startsWith("student_courses") == true -> "Courses"
                currentRoute?.startsWith("student_profile") == true -> "Profile"
                currentRoute?.startsWith("student_meeting") == true -> "Meetings"
                currentRoute?.startsWith("student_lessons") == true -> "Lessons"
                else -> "Student"
            }

            // Use the  Material3 TopAppBar
            TopAppBar(
                title = { Text(titleText) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFFEBEBF6),
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    // The return arrow is displayed only if the current route begins with "lessons"
                    if (currentRoute?.startsWith("student_lessons") == true) {
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
            StudentBottomBar(navController)
        }
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            StudentNavHost(navController,onLoginOut)
        }
    }
}
