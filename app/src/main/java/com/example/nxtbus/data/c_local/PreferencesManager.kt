package com.example.nxtbus.data.c_local


import android.content.Context
import android.content.SharedPreferences
import com.example.nxtbus.data.a_model.Booking
import com.example.nxtbus.data.a_model.Passenger
import com.example.nxtbus.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesManager(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences(
        Constants.PREFS_NAME, Context.MODE_PRIVATE
    )
    private val gson = Gson()

    fun saveBooking(booking: Booking) {
        val bookings = getBookings().toMutableList()
        bookings.add(booking)

        val bookingsJson = gson.toJson(bookings)
        preferences.edit()
            .putString(Constants.BOOKINGS_KEY, bookingsJson)
            .apply()
    }

    fun getBookings(): List<Booking> {
        val bookingsJson = preferences.getString(Constants.BOOKINGS_KEY, null)
        val raw = if (bookingsJson != null) {
            val type = object : TypeToken<List<Booking>>() {}.type
            gson.fromJson<List<Booking>>(bookingsJson, type)
        } else {
            emptyList()
        }
        // Normalize for backward compatibility
        return raw.map { b ->
            val seats = b.selectedSeats.orEmpty()
            val passengers: List<Passenger> = when {
                !b.passengers.isNullOrEmpty() -> b.passengers!!
                b.passenger != null -> listOf(b.passenger)
                else -> emptyList()
            }
            b.copy(
                selectedSeats = seats,
                passengers = passengers
            )
        }
    }

    fun getBookingByPNR(pnr: String): Booking? {
        return getBookings().find { it.pnr == pnr }
    }

    fun deleteBooking(pnr: String) {
        val updated = getBookings().filterNot { it.pnr == pnr }
        val bookingsJson = gson.toJson(updated)
        preferences.edit()
            .putString(Constants.BOOKINGS_KEY, bookingsJson)
            .apply()
    }
}