package com.example.tutoring.ui.screens.tutor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import com.example.tutoring.data.CourseStats
import com.example.tutoring.data.StudentProgress
import androidx.compose.animation.core.animateFloatAsState

import com.example.tutoring.data.mockCourses

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorDashboardScreen(courses: List<CourseStats> = mockCourses) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCourse by remember { mutableStateOf<CourseStats?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select a Course", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                readOnly = true,
                value = selectedCourse?.courseName ?: "Choose a course",
                onValueChange = {},
                label = { Text("Course") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                courses.forEach { course ->
                    DropdownMenuItem(
                        text = { Text(course.courseName) },
                        onClick = {
                            selectedCourse = course
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        selectedCourse?.let { course ->
            Text(
                "Enrolled Students: ${course.enrolledStudents}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            StudentProgressChart(course.studentProgressList)
        }
    }
}

@Composable
fun StudentProgressChart(progressList: List<StudentProgress>) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        progressList.forEach { student ->
            val targetProgress = student.lessonsCompleted.toFloat() / student.totalLessons
            val percent = (targetProgress * 100).toInt()

            val barColor = when {
                percent >= 100 -> Color(0xFF4CAF50) // Green
                percent >= 50 -> Color(0xFFFFA000) // Orange
                else -> Color(0xFFD32F2F)          // Red
            }

            val animatedProgress by animateFloatAsState(
                targetValue = targetProgress,
                animationSpec = tween(durationMillis = 800)
            )

            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(text = student.studentName)
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .background(Color.LightGray, shape = MaterialTheme.shapes.medium)
                ) {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgress)
                            .background(barColor, shape = MaterialTheme.shapes.medium)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("$percent% Complete", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    TutorDashboardScreen()
}
