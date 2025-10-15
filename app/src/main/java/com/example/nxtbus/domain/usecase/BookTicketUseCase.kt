package com.example.nxtbus.domain.usecase

import com.example.nxtbus.data.a_model.Booking
import com.example.nxtbus.domain.repository.IBookingRepository
import javax.inject.Inject

class BookTicketUseCase @Inject constructor(
    private val bookingRepository: IBookingRepository
) {
    suspend operator fun invoke(booking: Booking): Result<String> {
        return try {
            bookingRepository.saveBooking(booking)
            Result.success(booking.pnr)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}