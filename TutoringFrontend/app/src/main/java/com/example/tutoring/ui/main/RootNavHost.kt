package com.example.tutoring.ui.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tutoring.data.Role
import com.example.tutoring.ui.navigation.auth.AuthMainScreen
import com.example.tutoring.utils.ErrorDialog
import com.example.tutoring.utils.ErrorNotifier
import com.example.tutoring.utils.LoadingViewModel
import com.example.tutoring.utils.getRoleFromLogin
import com.example.tutoring.utils.logoutClear
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RootNavHost(loadingViewModel: LoadingViewModel = viewModel()) {
    val context = LocalContext.current
    // Automatically checks and restores user login status from startup
    val initialRole = getRoleFromLogin(context)
    var isLoggedIn by remember { mutableStateOf(initialRole != null) }
    var userRole by remember { mutableStateOf(initialRole ?: Role.STUDENT) }

    val navController = rememberNavController()

    // Error popup logic
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var errorMessage by remember { mutableStateOf<String?>(null) }
    // Sign up for Snack bar & AlertDialog
    ErrorNotifier.registerSnackbar { message ->
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }
    ErrorNotifier.registerAlertDialog { message ->
        errorMessage = message
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) {
            NavHost(
                navController = navController,
                startDestination = if (isLoggedIn) "main" else "auth"
            ) {
                composable("auth") {
                    AuthMainScreen(
                        onLoginSuccess = { role ->
                            // After the login is successful, the status is updated and the main page is displayed
                            isLoggedIn = true
                            userRole = role
                            navController.navigate("main") {
                                popUpTo("auth") { inclusive = true }
                            }
                        },
                        context
                    )
                }
                composable("main") {
                    // The main page is loaded based on the user role
                    MainScreen(
                        userRole = userRole,
                        onLoginOut = {
                            isLoggedIn = false
                            logoutClear(context)
                            navController.navigate("auth") {
                                popUpTo("main") { inclusive = true }
                            }
                        }
                    )
                }
            }
            // Listen and display a dialog box at the outermost layer of the page
            ErrorDialog(
                errorMessage = errorMessage,
                onDismiss = { errorMessage = null }
            )
        }
    }
}

