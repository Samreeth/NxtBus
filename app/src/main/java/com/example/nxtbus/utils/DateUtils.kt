package com.example.nxtbus.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private fun createDisplayFormat(): SimpleDateFormat {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

    private fun createBookingFormat(): SimpleDateFormat {
        return SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    }

    fun formatDateForDisplay(date: Date): String {
        return createDisplayFormat().format(date)
    }

    fun formatDateForBooking(date: Date): String {
        return createBookingFormat().format(date)
    }

    fun getCurrentDate(): String {
        return formatDateForDisplay(Date())
    }

    fun getCurrentBookingDate(): String {
        return formatDateForBooking(Date())
    }
}
