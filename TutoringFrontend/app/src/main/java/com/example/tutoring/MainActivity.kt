package com.example.tutoring

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.compose.TutoringTheme
import com.example.tutoring.network.NetworkClient
import com.example.tutoring.ui.main.RootNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NetworkClient.initialize(applicationContext) // 初始化 NetworkClient
        enableEdgeToEdge()
        setContent {
            TutoringTheme {
                RootNavHost()
            }
        }
    }
}