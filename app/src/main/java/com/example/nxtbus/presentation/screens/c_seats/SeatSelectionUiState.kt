package com.example.nxtbus.presentation.screens.c_seats

import com.example.nxtbus.data.a_model.Bus
import com.example.nxtbus.data.a_model.Seat

data class SeatSelectionUiState(
    val bus: Bus? = null,
    val seats: List<Seat> = emptyList(),
    val selectedSeats: List<Seat> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val fromCity: String = "",
    val toCity: String = "",
    val selectedDate: String = "",
    val selectedTab: SeatTab = SeatTab.SLEEPER,
    val totalAmount: Int = 0
)

enum class SeatTab {
    SLEEPER, SEATER
}