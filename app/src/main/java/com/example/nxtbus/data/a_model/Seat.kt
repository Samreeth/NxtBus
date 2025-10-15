package com.example.nxtbus.data.a_model

data class Seat(
    val seatNumber: String,
    val isAvailable: Boolean = true,
    val isSelected: Boolean = false,
    val seatType: SeatType = SeatType.SEATER,
    val price: Int = 0
)

enum class SeatType {
    SEATER, SLEEPER
}