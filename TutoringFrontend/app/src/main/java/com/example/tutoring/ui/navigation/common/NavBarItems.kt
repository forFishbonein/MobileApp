package com.example.tutoring.ui.navigation.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import com.example.tutoring.ui.navigation.student.StudentNavRoutes
import com.example.tutoring.ui.navigation.tutor.TutorNavRoutes

object NavBarItems {

    val StudentBarItems = listOf(
        BarItem(
            title = "Home",
            image = Icons.Filled.Home,
            route = StudentNavRoutes.Home.route
        ),
        BarItem(
            title = "Courses",
            image = Icons.AutoMirrored.Filled.LibraryBooks,
            route = StudentNavRoutes.Courses.route
        ),
        BarItem(
            title = "Profile",
            image = Icons.Filled.Person,
            route = StudentNavRoutes.Profile.route
        )
    )

    val TutorBarItems = listOf(
        BarItem(
            title = "Home",
            image = Icons.Filled.Home,
            route = TutorNavRoutes.Home.route
        ),
        BarItem(
            title = "Application",
            image = Icons.AutoMirrored.Filled.Send,
            route = TutorNavRoutes.Application.route
        ),
        BarItem(
            title = "Courses",
            image = Icons.Filled.Book,
            route = TutorNavRoutes.Courses.route
        ),
        BarItem(
            title = "Profile",
            image = Icons.Filled.Person,
            route = TutorNavRoutes.Profile.route
        ),
    )
}
