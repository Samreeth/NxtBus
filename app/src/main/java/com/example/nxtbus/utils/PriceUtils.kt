package com.example.nxtbus.utils

import com.example.nxtbus.data.a_model.Seat

object PriceUtils {

    fun formatPrice(price: Int): String {
        return "₹$price"
    }

    fun calculateTotalPrice(seats: List<Seat>): Int {
        return seats.sumOf { it.price }
    }

    fun generatePNR(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..10)
            .map { chars.random() }
            .joinToString("")
    }
}