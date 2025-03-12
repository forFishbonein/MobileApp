package com.example.tutoring.ui.screens.student.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tutoring.data.Course

@Composable
fun CourseCard(cardType:String="home", course: Course, onJoinClick: () -> Unit, navController: NavHostController? = null) {
    // 新增展开状态
    var expanded by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.6f) // Lower the opacity to make the border softer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = course.courseName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Subject: ${course.subject}",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (course.status.isNotBlank() && cardType=="home") {
                    Text(
                        text = "Status: ${course.status}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = if (expanded) "Less Info" else "More Info...",
                    style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .clickable {
                            expanded = !expanded
                        }
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(cardType=="home"){
                    Button(
                        onClick = {
                            showConfirmDialog = true
                        },
                        shape = RoundedCornerShape(50),
//                        enabled = course.status.isBlank() || course.status == "rejected",
                        enabled = course.status.isBlank(),
                    ) {
                        Text("Join")
                    }
                }else{
                    Button(
                        onClick = {
                            navController?.navigate("student_lessons/${course.courseId}")
                        },
                        shape = RoundedCornerShape(50),
                    ) {
                        Text("Check")
                    }
                }
            }
        }

        if (expanded) {
            if(cardType=="home"){
                Text(
                    text = "TutorName: ${if (course.teacherName.isNullOrBlank()) "Not available" else course.teacherName}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(start = 16.dp)
                )
            }
            Text(
                text = "Description: ${course.description}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(start = 16.dp)
            )
            Text(
                text = "Create Time: ${if (course.createdAt.isNullOrBlank()) "Not available" else course.createdAt}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(start = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmation") },
            text = { Text("Are you sure you want to join this course?") },
            confirmButton = {
                Button(
                    onClick = {
                        onJoinClick()
                        showConfirmDialog = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}