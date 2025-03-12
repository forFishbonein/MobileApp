package com.example.tutoring.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoadingViewModel : ViewModel() {
    var isHttpLoading by mutableStateOf(false)
        private set
    fun setLoading(loading: Boolean) {
        isHttpLoading = loading
    }
}
