package com.example.nxtbus.utils

object Constants {
    const val PREFS_NAME = "nxtbus_preferences"
    const val BOOKINGS_KEY = "bookings"

    val CITIES = listOf(
        "Mumbai", "Delhi", "Bengaluru", "Chennai", "Pune", "Hyderabad",
        "Kolkata", "Ahmedabad", "Surat", "Jaipur"
    )

    val GENDERS = listOf("Male", "Female", "Other")

    const val MAX_SEAT_SELECTION = 6
    const val MIN_AGE = 1
    const val MAX_AGE = 120
}