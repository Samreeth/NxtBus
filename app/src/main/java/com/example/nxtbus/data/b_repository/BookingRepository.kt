package com.example.nxtbus.data.b_repository


import com.example.nxtbus.data.c_local.PreferencesManager
import com.example.nxtbus.data.a_model.Booking
import com.example.nxtbus.domain.repository.IBookingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepository @Inject constructor(
    private val preferencesManager: PreferencesManager
) : IBookingRepository {

    override suspend fun saveBooking(booking: Booking) {
        preferencesManager.saveBooking(booking)
    }

    override suspend fun getBookings(): List<Booking> {
        return preferencesManager.getBookings()
    }

    override suspend fun getBookingByPNR(pnr: String): Booking? {
        return preferencesManager.getBookingByPNR(pnr)
    }

    override suspend fun deleteBooking(pnr: String) {
        preferencesManager.deleteBooking(pnr)
    }
}