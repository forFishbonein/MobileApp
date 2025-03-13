package com.example.tutoring.ui.screens.auth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tutoring.data.Role
import com.example.tutoring.network.ApiService
import com.example.tutoring.network.NetworkClient
import com.example.tutoring.utils.ErrorNotifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    var codeCountdown by remember { mutableStateOf(0) }
    val isCodeButtonEnabled = codeCountdown == 0

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var userType by remember { mutableStateOf(Role.STUDENT) }
    val apiService = NetworkClient.createService(ApiService::class.java)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Join us!", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = (userType == Role.STUDENT),
                    onClick = { userType = Role.STUDENT }
                )
                Text("Student", modifier = Modifier.padding(start = 2.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = (userType == Role.TUTOR),
                    onClick = { userType = Role.TUTOR }
                )
                Text("Tutor", modifier = Modifier.padding(start = 2.dp))
            }
        }


        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            singleLine = true,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = verificationCode,
                onValueChange = { verificationCode = it },
                label = { Text("Verification Code") },
                singleLine = true,
                modifier = Modifier.weight(1f) // Allocate surplus space
            )

            Button(
                onClick = {
                    if (isCodeButtonEnabled && email.isNotBlank()) {
                        codeCountdown = 60
                        coroutineScope.launch {
                            while (codeCountdown > 0) {
                                delay(1000L)
                                codeCountdown--
                            }
                        }
                        coroutineScope.launch {
                            try {
                                // Call backend API to send verification code to the provided email.
                                val requestBody = mapOf("email" to email)
                                val response = apiService.sendCode(requestBody)
                                ErrorNotifier.showSuccess("Send successful! Please check your email.")
                            } catch (e: Exception) {
                                ErrorNotifier.showError(e.message ?: "Send failed.")
                            }
                        }
                    }
                },
                enabled = isCodeButtonEnabled && isEmailValid(email),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.wrapContentWidth()
                .height(56.dp)
                .padding(top = 6.dp)
            ) {
                if (isCodeButtonEnabled && isEmailValid(email)) {
                    Text("Get Code")
                } else if (!isCodeButtonEnabled) {
                    Text("Retry in ${codeCountdown}s")
                } else {
                    Text("Get Code")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                    ErrorNotifier.showError("Please fill all fields")
                } else if (password != confirmPassword) {
                    ErrorNotifier.showError("Passwords do not match")
                } else {
                    // Registration logic goes here
                    coroutineScope.launch {
                        try {
                            // Call backend API to send verification code to the provided email.
                            val requestBody = mapOf(
                                "code" to verificationCode,
                                "email" to email,
                                "password" to password,
                                "role" to userType.name.lowercase()
                            )
                            val response = apiService.register(requestBody)
                            ErrorNotifier.showSuccess("Register successful!")
                            onNavigateToLogin()
                        } catch (e: Exception) {
                            ErrorNotifier.showError(e.message ?: "Register failed.")
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = { onNavigateToLogin() },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Already have an account? Login")
        }
    }

}
// Mailbox format verification function
fun isEmailValid(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
