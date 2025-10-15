package com.example.nxtbus.domain.repository

import com.example.nxtbus.data.a_model.Bus
import com.example.nxtbus.data.a_model.Seat


interface IBusRepository {
    suspend fun searchBuses(fromCity: String, toCity: String, date: String): List<Bus>
    suspend fun getBusById(busId: String, fromCity: String, toCity: String, date: String): Bus?
    suspend fun getSeatsForBus(bus: Bus): List<Seat>
}