package com.example.tutoring.ui.screens.tutor
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.tutoring.data.Lesson
import com.example.tutoring.network.ApiService
import com.example.tutoring.network.NetworkClient
import com.example.tutoring.utils.ErrorNotifier
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

data class LessonRequest(
    val isCompleted: Boolean = false,
    val content: String = "",
    val courseId: Int = 0,
    val imageUrls: String = "",
    val pdfUrls: String = "",
    val title: String = "",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLessonScreen(
    navController: NavHostController,
    courseId: Int?,
    lesson: Lesson?
) {
    // Check whether the id is -1 when you enter the page for the first time to determine whether the page is updated or added
    val isUpdate = courseId == -1
    val context = androidx.compose.ui.platform.LocalContext.current
    // Initializes each state variable
    var lessonTitle by remember { mutableStateOf(lesson?.title ?: "") }
    var contentText by remember { mutableStateOf(lesson?.content ?: "") }
    var imageUrls by remember { mutableStateOf(lesson?.imageUrls?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()) }
    var pdfUrls by remember { mutableStateOf(lesson?.pdfUrls?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()) }

    val coroutineScope = rememberCoroutineScope()
    val apiService = NetworkClient.createService(ApiService::class.java)



    // Image selector: Allows users to select images
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                val mimeType = context.contentResolver.getType(it) ?: "application/octet-stream"
                val extension = android.webkit.MimeTypeMap.getSingleton()
                    .getExtensionFromMimeType(mimeType) ?: "jpg"
                // Use ContentResolver to read the contents of the file
                val inputStream = context.contentResolver.openInputStream(it)
                val fileBytes = inputStream?.readBytes()
                inputStream?.close()

                if (fileBytes != null) {
                    // Build RequestBody and MultipartBody.Part, generated from a dynamic MIME type
                    val requestBody = fileBytes.toRequestBody(mimeType.toMediaTypeOrNull())
                    val multipartPart = MultipartBody.Part.createFormData(
                        "file",
                        "image_${System.currentTimeMillis()}.$extension",
                        requestBody
                    )
                    coroutineScope.launch {
                        try {
                            val response = apiService.uploadImage(multipartPart)
                            // After the upload succeeds, the URL of the returned image is added to the list
                            imageUrls = imageUrls + response.data.toString()
                            ErrorNotifier.showSuccess("Upload Successful!")
                        } catch (e: Exception) {
                            ErrorNotifier.showError("Upload exception: ${e.message}")
                        }
                    }
                } else {
                    ErrorNotifier.showError("File reading failed")
                }
            }
        }
    )

    // PDF file selector
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                // Gets MIME type, default "application/pdf"
                val mimeType = context.contentResolver.getType(it) ?: "application/pdf"
                // Get extension based on MIME type, default "pdf"
                val extension = android.webkit.MimeTypeMap.getSingleton()
                    .getExtensionFromMimeType(mimeType) ?: "pdf"

                // Use ContentResolver to read the contents of the file
                val inputStream = context.contentResolver.openInputStream(it)
                val fileBytes = inputStream?.readBytes()
                inputStream?.close()

                if (fileBytes != null) {
                    // Build the RequestBody and MultipartBody.Part
                    val requestBody = fileBytes.toRequestBody(mimeType.toMediaTypeOrNull())
                    val multipartPart = MultipartBody.Part.createFormData(
                        "file",
                        "pdf_${System.currentTimeMillis()}.$extension",
                        requestBody
                    )
                    coroutineScope.launch {
                        try {
                            val response = apiService.uploadPdf(multipartPart)
                            // After the upload is successful, the returned PDF URL is added to the list
                            pdfUrls = pdfUrls + response.data.toString()
                            ErrorNotifier.showSuccess("Upload Successful!")
                        } catch (e: Exception) {
                            ErrorNotifier.showError("Upload exception: ${e.message}")
                        }
                    }
                } else {
                    ErrorNotifier.showError("File reading failed")
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Roll enabled
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = lessonTitle,
            onValueChange = { lessonTitle = it },
            label = { Text("Lesson Title") },
            modifier = Modifier
                .fillMaxWidth()
        )
        OutlinedTextField(
            value = contentText,
            onValueChange = { contentText = it },
            label = { Text("Lesson Content") },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(top = 8.dp)
        )
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                ) {
                    Text("Upload Image")
                }
                // Displays the uploaded image area
                if (imageUrls.isNotEmpty()) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Uploaded Images:", style = MaterialTheme.typography.titleMedium)
                            // Loop through the imageUrls and display the image using Coil AsyncImage
                            imageUrls.forEach { url ->
                                AsyncImage(
                                    model = url,
                                    contentDescription = "Uploaded Image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                )
                            }
                        }
                    }
                }

            }
        }
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { pdfPickerLauncher.launch("application/pdf") },
                ) {
                    Text("Upload PDF")
                }
                // Displays the uploaded PDF area
                if (pdfUrls.isNotEmpty()) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Uploaded PDFs:", style = MaterialTheme.typography.titleMedium)
                            // Traverse the pdfUrls and display them as clickable links
                            pdfUrls.forEachIndexed { index, url ->
                                val fileName = url.substringAfterLast("_")
                                TextButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            data = Uri.parse(url)
                                        }
                                        context.startActivity(intent)
                                    }
                                ) {
                                    Text("${index + 1}. $fileName", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                if(!isUpdate){
                    coroutineScope.launch {
                        try {
                            val lessonRequest = courseId?.let {
                                Lesson(
                                    completed = false,
                                    content = contentText,
                                    courseId = it,
                                    title = lessonTitle,
                                    imageUrls = imageUrls.joinToString(separator = ","),
                                    pdfUrls = pdfUrls.joinToString(separator = ",")
                                )
                            }
                            if (lessonRequest != null) {
                                val response = apiService.createLesson(lessonRequest)
                                ErrorNotifier.showSuccess("Create Lesson Successful!")
                                navController.popBackStack() //Go back to previous page
                            }else{
                                ErrorNotifier.showError( "Create Failed.")
                            }
                        } catch (e: Exception) {
                            ErrorNotifier.showError(e.message ?: "Create Failed.")
                        }
                    }
                }else{
                    coroutineScope.launch {
                        try {
                            val lessonRequest = lesson?.let {
                                LessonRequest(
                                    isCompleted = it.completed,
                                    content = contentText,
                                    courseId = it.courseId,
                                    title = lessonTitle,
                                    imageUrls = imageUrls.joinToString(separator = ","),
                                    pdfUrls = pdfUrls.joinToString(separator = ",")
                                )
                            }

                            if (lessonRequest != null) {
                                val response = apiService.updateLesson(lessonRequest, lesson.lessonId)
                                ErrorNotifier.showSuccess("Update Lesson Successful!")
                                navController.popBackStack() //Go back to previous page
                            }else{
                                ErrorNotifier.showError( "Update Failed.")
                            }
                        } catch (e: Exception) {
                            ErrorNotifier.showError(e.message ?: "Update Failed.")
                        }
                    }
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text( text = if (isUpdate) "Update Lesson" else "Submit Lesson")
        }
    }
}
