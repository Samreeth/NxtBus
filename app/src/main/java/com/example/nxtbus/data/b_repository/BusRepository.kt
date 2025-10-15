package com.example.nxtbus.data.b_repository

import com.example.nxtbus.data.a_model.Bus
import com.example.nxtbus.data.a_model.Seat
import com.example.nxtbus.domain.repository.IBusRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BusRepository @Inject constructor() : IBusRepository {

    // Cache search results by (from,to,date) so selections remain consistent across screens
    private val searchCache = mutableMapOf<String, List<Bus>>()

    private fun normalizeDate(date: String): String = date.replace("-", "/")
    private fun keyOf(fromCity: String, toCity: String, date: String): String =
        listOf(fromCity.trim(), toCity.trim(), normalizeDate(date).trim()).joinToString("|")

    override suspend fun searchBuses(fromCity: String, toCity: String, date: String): List<Bus> {
        val key = keyOf(fromCity, toCity, date)
        searchCache[key]?.let { return it }

        // Simulate network delay
        delay(1500)
        val generated = MockDataSource.generateBuses(fromCity, toCity, normalizeDate(date))
        searchCache[key] = generated
        return generated
    }

    override suspend fun getBusById(busId: String, fromCity: String, toCity: String, date: String): Bus? {
        val key = keyOf(fromCity, toCity, date)
        // Prefer cached list from the search screen to guarantee consistency
        val cached = searchCache[key]
        if (cached != null) return cached.find { it.id == busId }

        // Fallback: trigger search (will cache) and then find
        val buses = searchBuses(fromCity, toCity, date)
        return buses.find { it.id == busId }
    }

    override suspend fun getSeatsForBus(bus: Bus): List<Seat> {
        // Simulate slight delay for seat loading
        delay(500)
        return MockDataSource.generateSeatsForBus(bus)
    }
}