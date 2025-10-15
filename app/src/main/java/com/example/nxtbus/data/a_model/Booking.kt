package com.example.nxtbus.data.a_model

data class Booking(
    val pnr: String,
    val bus: Bus,
    // Nullable for backward compatibility; use .orEmpty() at call sites
    val selectedSeats: List<String>? = emptyList(),
    // For backward compatibility with older saved data
    val passenger: Passenger? = null,
    // Nullable for backward compatibility; use .orEmpty() at call sites
    val passengers: List<Passenger>? = emptyList(),
    val contactDetails: ContactDetails,
    val journeyDate: String,
    val fromCity: String,
    val toCity: String,
    val totalAmount: Int,
    val bookingDate: String,
    val status: BookingStatus = BookingStatus.CONFIRMED
)

data class ContactDetails(
    val mobileNumber: String,
    val emailAddress: String
)
enum class BookingStatus {
    CONFIRMED, CANCELLED, COMPLETED
}