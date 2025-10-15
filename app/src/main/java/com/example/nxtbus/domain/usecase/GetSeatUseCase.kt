package com.example.nxtbus.domain.usecase

import com.example.nxtbus.data.a_model.Bus
import com.example.nxtbus.data.a_model.Seat
import com.example.nxtbus.domain.repository.IBusRepository
import javax.inject.Inject

class GetSeatsUseCase @Inject constructor(
    private val busRepository: IBusRepository
) {
    suspend operator fun invoke(bus: Bus): Result<List<Seat>> {
        return try {
            val seats = busRepository.getSeatsForBus(bus)
            Result.success(seats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}