package com.example.nxtbus.domain.repository

import com.example.nxtbus.data.a_model.Booking


interface IBookingRepository {
    suspend fun saveBooking(booking: Booking)
    suspend fun getBookings(): List<Booking>
    suspend fun getBookingByPNR(pnr: String): Booking?
    suspend fun deleteBooking(pnr: String)
}
