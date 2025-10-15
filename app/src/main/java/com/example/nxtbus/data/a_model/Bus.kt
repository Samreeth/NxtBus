package com.example.nxtbus.data.a_model

data class Bus(
    val id: String,
    val operatorName: String,
    val busType: String,
    val departureTime: String,
    val arrivalTime: String,
    val duration: String,
    val distance: String,
    val price: Int,
    val availableSeats: Int,
    val totalSeats: Int = if (busType.contains("Sleeper")) 24 else 47,
    val amenities: List<String> = listOf(),
    val rating: Float = 0f
)

