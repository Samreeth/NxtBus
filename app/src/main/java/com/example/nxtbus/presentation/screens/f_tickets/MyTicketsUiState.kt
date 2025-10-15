package com.example.nxtbus.presentation.screens.f_tickets

import com.example.nxtbus.data.a_model.Booking

data class MyTicketsUiState(
    val bookings: List<Booking> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)