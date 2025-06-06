package com.example.tutoring

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.example.compose.TutoringTheme
import com.example.tutoring.network.NetworkClient
import com.example.tutoring.ui.main.RootNavHost


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NetworkClient.initialize(applicationContext) // Initialize NetworkClient
        enableEdgeToEdge()
        setContent {
            TutoringTheme {
                RootNavHost()
            }
        }
    }
}