package com.example.nxtbus.presentation.screens.e_confirmation

import com.example.nxtbus.data.a_model.Booking

data class BookingConfirmationUiState(
    val booking: Booking? = null,
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)
