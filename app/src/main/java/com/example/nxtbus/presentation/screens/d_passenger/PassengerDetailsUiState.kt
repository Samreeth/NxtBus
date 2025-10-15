package com.example.nxtbus.presentation.screens.d_passenger

import com.example.nxtbus.data.a_model.Bus

// Represents the input fields and validation errors for one passenger
data class PassengerInput(
    val name: String = "",
    val age: String = "",
    val gender: String = "",
    val seatNumber: String = "",
    val nameError: String = "",
    val ageError: String = ""
)

data class PassengerDetailsUiState(
    val bus: Bus? = null,
    val selectedSeats: List<String> = emptyList(),
    val totalAmount: Int = 0,
    val fromCity: String = "",
    val toCity: String = "",
    val selectedDate: String = "",

    // Multiple passenger inputs, one per seat
    val passengers: List<PassengerInput> = emptyList(),

    // Contact details (shared)
    val mobileNumber: String = "",
    val emailAddress: String = "",

    // Validation errors for contact
    val mobileError: String = "",
    val emailError: String = "",

    // Optional add-ons
    val includeRefund: Boolean = false, // +₹39 if true
    val includeAckoInsurance: Boolean = false, // +₹10 per passenger if true

    // UI states
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val isBooking: Boolean = false
)
