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
import com.example.tutoring.ui.screens.tutor.CourseRegistration

// 课程卡片
@Composable
fun CourseCardTutor(cardType:String="application", course: CourseRegistration, onConfirmClick: (confirm:String) -> Unit, navController: NavHostController? = null) {
    // 新增展开状态
    var expanded by remember { mutableStateOf(false) }
    // 控制是否显示“Accept”确认对话框
    var showAcceptDialog by remember { mutableStateOf(false) }
    // 控制是否显示“Reject”确认对话框
    var showRejectDialog by remember { mutableStateOf(false) }
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // 阴影从8dp改为2dp
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.6f) // 调低不透明度，让边框更柔和
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
                        // 处理点击事件
                        expanded = !expanded
                    }
                )
            }
            // 将按钮和状态标签放在同一个 Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(cardType=="application"){
                    Button(
                        onClick = {
                            // 触发加入课程逻辑
                            showAcceptDialog = true
                        },
                        shape = RoundedCornerShape(50),
                        enabled = course.status == "pending",
                    ) {
                        Text("Accept")
                    }
                    Button(
                        onClick = {
                            // 触发加入课程逻辑
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
                            // 跳转到 lessons
                            navController?.navigate("tutor_lessons/${course.courseId}")
                        },
                        shape = RoundedCornerShape(50),
                    ) {
                        Text("Check")
                    }
                }
            }
        }
        // 添加展开更多信息按钮

        // 展开区域
        if (expanded) {
            // 这里展示更多信息，可根据需要修改
            if(cardType=="application"){
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TutorId: ${course.tutorId}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(start = 16.dp)
                    )
                    Text(
                        text = "Student Id: ${course.studentId}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(start = 16.dp)
                    )
                }
            }
            //这里返回值没有 TutorName
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
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
    // Accept 确认对话框
    if (showAcceptDialog) {
        AlertDialog(
            onDismissRequest = { showAcceptDialog = false },
            title = { Text("Confirmation") },
            text = { Text("Are you sure you want to accept this course request?") },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirmClick("approved")   // 用户确认后调用函数
                        showAcceptDialog = false  // 关闭对话框
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

    // Reject 确认对话框
    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Confirmation") },
            text = { Text("Are you sure you want to reject this course request?") },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirmClick("rejected")    // 用户确认后调用函数
                        showRejectDialog = false  // 关闭对话框
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
}