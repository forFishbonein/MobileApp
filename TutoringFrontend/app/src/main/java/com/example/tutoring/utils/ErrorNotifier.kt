package com.example.tutoring.utils

import android.widget.Toast
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch

// ErrorNotifier.kt
object ErrorNotifier {
    private var showSnackbar: ((String) -> Unit)? = null
    private var showAlertDialog: ((String) -> Unit)? = null

    fun registerSnackbar(showSnackbarAction: (String) -> Unit) {
        showSnackbar = showSnackbarAction
    }

    fun registerAlertDialog(showAlertDialogAction: (String) -> Unit) {
        showAlertDialog = showAlertDialogAction
    }

    fun showError(message: String, useDialog: Boolean = false) {
        if (useDialog) {
            showAlertDialog?.invoke(message)
        } else {
            showSnackbar?.invoke(message)
        }
    }
    fun showSuccess(message: String) {
        showSnackbar?.invoke(message)
    }
}

//try {
//    val result = apiService.getData()
//} catch (e: Exception) {
//    ErrorNotifier.showError("数据加载失败，请稍后重试")
//}


//ErrorNotifier.showError(message, useDialog = true)



