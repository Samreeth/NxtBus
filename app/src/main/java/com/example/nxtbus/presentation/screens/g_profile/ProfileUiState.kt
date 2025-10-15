package com.example.nxtbus.presentation.screens.g_profile

data class ProfileUiState(
    val userName: String = "Guest User",
    val userEmail: String = "",
    val userPhone: String = "",
    val totalBookings: Int = 0
)