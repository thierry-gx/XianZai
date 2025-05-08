package com.thierryguichardaz.xianzai.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle // Icona Profilo
import androidx.compose.material.icons.filled.CalendarMonth // Icona Calendario
import androidx.compose.material.icons.filled.Home // Icona Home
import androidx.compose.ui.graphics.vector.ImageVector

// Sealed class per rappresentare gli item della bottom navigation
sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = "home",
        label = "Home",
        icon = Icons.Default.Home
    )

    object Calendar : BottomNavItem(
        route = "calendar",
        label = "Calendar",
        icon = Icons.Default.CalendarMonth
    )

    object Profile : BottomNavItem(
        route = "profile",
        label = "Profile",
        icon = Icons.Default.AccountCircle
    )
}

// Lista degli item per iterare facilmente
val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Calendar,
    BottomNavItem.Profile
)