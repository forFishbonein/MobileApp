package com.example.tutoring.ui.screens.student.common

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tutoring.data.Lesson
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonCard(
    lesson: Lesson,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = lesson.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.2.sp,
                    shadow = Shadow(
                        color = Color.Gray,
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(modifier = Modifier
                .padding(16.dp)
                .heightIn(min = 375.dp)){
                Text(
                    text = lesson.content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 0.5.sp,
                        lineHeight = 22.sp
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                if (lesson.imageUrls.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Relevant Images:", style = MaterialTheme.typography.bodyMedium)
                        // Loop through the imageUrls and display the image using Coil AsyncImage
                        lesson.imageUrls.split(",").forEach { url ->
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
                if (lesson.pdfUrls.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Relevant PDFs:", style = MaterialTheme.typography.bodyMedium)
                        lesson.pdfUrls.split(",").forEachIndexed { index, url ->
                            // Gets the part after the underscore as the file name
                            val fileName = url.substringAfterLast("_")
                            TextButton(
                                onClick = {
                                    // Click and open the browser to view the PDF, using Android Intent mode
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse(url)
                                    }
                                    context.startActivity(intent)
                                }
                            ) {
                                // Display like: "1.myfile.pdf"
                                Text("${index + 1}. $fileName", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
            Text(
                text = "Status: ${if (lesson.completed) "completed" else "in progress"}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
            )
        }
    }
}

