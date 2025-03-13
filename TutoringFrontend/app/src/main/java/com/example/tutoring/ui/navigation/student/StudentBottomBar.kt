package com.example.tutoring.ui.navigation.student

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.tutoring.ui.navigation.common.NavBarItems

@Composable
fun StudentBottomBar(
    navController: NavHostController
) {
    // Gets the current route information to determine the selected item
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = Color.White
    ) {
        NavBarItems.StudentBarItems.forEach { navItem ->
            NavigationBarItem(
                selected = currentRoute?.startsWith(navItem.route) == true,
                onClick = {
                    // Click to navigate to the corresponding route
                    navController.navigate(navItem.route) {
                        // Skip configuration
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = navItem.image,
                        contentDescription = navItem.title
                    )
                },
                label = {
                    Text(text = navItem.title)
                }
            )
        }
    }
}
