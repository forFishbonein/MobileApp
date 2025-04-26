package com.example.tutoring.ui.screens.tutor.common

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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tutoring.data.Course
import com.example.tutoring.network.ApiService
import com.example.tutoring.network.NetworkClient
import com.example.tutoring.ui.screens.tutor.CourseRegistration
import com.example.tutoring.utils.ErrorNotifier
import kotlinx.coroutines.launch

// 课程卡片
@Composable
fun CourseCardTutor(cardType:String="application",
                    course: CourseRegistration,
                    onConfirmClick: (confirm:String) -> Unit,
                    navController: NavHostController? = null,
                    onDelete: () -> Unit,
                    onUpdate: (course: CourseRegistration) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var showAcceptDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val apiService = NetworkClient.createService(ApiService::class.java)
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
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
                if (course.status.isNotBlank() && cardType=="application") {
                    Text(
                        text = course.status,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = if (expanded) "Less Info" else "More Info...",
                    style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Start).clickable {
                        expanded = !expanded
                    }
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(cardType=="application"){
                    Button(
                        onClick = {
                            showAcceptDialog = true
                        },
                        shape = RoundedCornerShape(50),
                        enabled = course.status == "pending",
                    ) {
                        Text("Accept")
                    }
                    Button(
                        onClick = {
                            showRejectDialog = true
                        },
                        shape = RoundedCornerShape(50),
                        enabled = course.status == "pending",
                    ) {
                        Text("Reject")
                    }
                }else{
                    Button(
                        onClick = {
                            navController?.navigate("tutor_lessons/${course.courseId}")
                        },
                        shape = RoundedCornerShape(50),
                    ) {
                        Text("Check")
                    }
                }
            }
        }

        if (expanded) {
            if(cardType=="application"){
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "StudentName: ${course.studentName}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(start = 16.dp)
                    )
//                    Text(
//                        text = "Student Id: ${course.studentId}",
//                        style = MaterialTheme.typography.bodyMedium,
//                        modifier = Modifier
//                            .padding(start = 16.dp)
//                    )
                }
            }
            // The return value does not have a TutorName!
//            else{
//                Text(
//                    text = "TutorName: ${if (course.tutorName.isBlank()) "Not available" else course.tutorName}",
//                    style = MaterialTheme.typography.bodyMedium,
//                    modifier = Modifier
//                        .padding(start = 16.dp)
//                )
//            }

            Text(
                text = "Description: ${course.description}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(start = 16.dp)
            )
            Text(
                text = "Create Time: ${course.createdAt.orEmpty().ifBlank { "Not available" }}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(start = 16.dp)
            )
            if(cardType=="courses"){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            onUpdate(course)
                            expanded = false
                        },
                        shape = RoundedCornerShape(8),
                        modifier = Modifier
                            .padding(end = 16.dp)
                    ) {
                        Text("Update")
                    }
                    Button(
                        onClick = {
                            showDeleteDialog = true
                        },
                        shape = RoundedCornerShape(8),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier
                        .padding(end = 16.dp)
                    ) {
                        Text("Delete")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    //Approve Dialog
    if (showAcceptDialog) {
        AlertDialog(
            onDismissRequest = { showAcceptDialog = false },
            title = { Text("Confirmation") },
            text = { Text("Are you sure you want to accept this course request?") },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirmClick("approved")
                        showAcceptDialog = false
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAcceptDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    //Reject Dialog
    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Confirmation") },
            text = { Text("Are you sure you want to reject this course request?") },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirmClick("rejected")
                        showRejectDialog = false
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    //Approve Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmation") },
            text = { Text("Are you sure you want to delete this course?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}