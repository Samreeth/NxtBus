package com.example.nxtbus.domain.usecase

import com.example.nxtbus.data.a_model.Bus
import com.example.nxtbus.domain.repository.IBusRepository
import javax.inject.Inject

class SearchBusesUseCase @Inject constructor(
    private val busRepository: IBusRepository
) {
    suspend operator fun invoke(fromCity: String, toCity: String, date: String): Result<List<Bus>> {
        return try {
            val buses = busRepository.searchBuses(fromCity, toCity, date)
            Result.success(buses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}