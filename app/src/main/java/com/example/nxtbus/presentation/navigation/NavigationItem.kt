package com.example.nxtbus.presentation.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.nxtbus.presentation.navigation.Screen


data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val screen: Screen
)

val bottomNavigationItems = listOf(
    NavigationItem(
        title = "Home",
        icon = Icons.Filled.Home,
        screen = Screen.Home
    ),
    NavigationItem(
        title = "My Tickets",
        icon = Icons.Filled.ConfirmationNumber,
        screen = Screen.MyTickets
    ),
    NavigationItem(
        title = "Profile",
        icon = Icons.Filled.Person,
        screen = Screen.Profile
    )
)