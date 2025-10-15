package com.example.nxtbus.data.b_repository

import com.example.nxtbus.data.a_model.Bus
import com.example.nxtbus.data.a_model.Seat
import com.example.nxtbus.data.a_model.SeatType
import kotlin.random.Random

object MockDataSource {

    private val busOperators = listOf(
        "Blue Bus", "Green Bus", "Yellow Bus", "Red Bus Express",
        "Bharathi Travels", "Orange Travels", "Kallada Travels",
        "Parveen Travels", "VRL Travels", "SRS Travels"
    )

    private val busTypes = listOf(
        "A/C Seater", "Non A/C Seater", "A/C Sleeper",
        "Non A/C Sleeper", "Volvo Multi-Axle A/C Sleeper"
    )

    private val amenities = listOf(
        "WiFi", "Charging Point", "AC", "Blanket", "Water Bottle",
        "Snacks", "Entertainment", "Reading Light"
    )

    fun generateBuses(fromCity: String, toCity: String, date: String): List<Bus> {
        val buses = mutableListOf<Bus>()
        val busCount = Random.nextInt(8, 15) // Generate 8-14 buses

        repeat(busCount) { index ->
            val busType = busTypes.random()
            val isSleeper = busType.contains("Sleeper")
            val isAC = busType.contains("A/C")

            // Generate realistic departure times
            val hour = Random.nextInt(6, 24) // 6 AM to 11 PM
            val minute = listOf(0, 15, 30, 45).random()
            val departureTime = String.format("%02d:%02d", hour, minute)

            // Calculate arrival time (8-12 hours journey)
            val journeyHours = Random.nextInt(8, 13)
            val arrivalHour = (hour + journeyHours) % 24
            val arrivalTime = String.format("%02d:%02d", arrivalHour, minute)

            // Generate price based on bus type
            val basePrice = when {
                isSleeper && isAC -> Random.nextInt(1000, 1500)
                isSleeper && !isAC -> Random.nextInt(700, 1100)
                !isSleeper && isAC -> Random.nextInt(600, 900)
                else -> Random.nextInt(400, 700)
            }

            buses.add(
                Bus(
                    id = "BUS${String.format("%03d", index + 1)}",
                    operatorName = busOperators.random(),
                    busType = busType,
                    departureTime = departureTime,
                    arrivalTime = arrivalTime,
                    duration = "${journeyHours}h 0m",
                    distance = "${Random.nextInt(400, 600)}km",
                    price = basePrice,
                    availableSeats = Random.nextInt(5, if (isSleeper) 20 else 35),
                    totalSeats = if (isSleeper) 24 else 47,
                    amenities = amenities.shuffled().take(Random.nextInt(2, 6)),
                    rating = Random.nextFloat() * 2 + 3 // 3.0 to 5.0 rating
                )
            )
        }

        return buses.sortedBy { it.departureTime }
    }

    fun generateSeatsForBus(bus: Bus): List<Seat> {
        val seats = mutableListOf<Seat>()
        val isSleeper = bus.busType.contains("Sleeper")
        val totalSeats = bus.totalSeats
        val bookedSeats = totalSeats - bus.availableSeats
        val bookedSeatNumbers = (1..totalSeats).shuffled().take(bookedSeats)

        if (isSleeper) {
            // Sleeper bus layout: L1-L12, R1-R12
            for (i in 1..12) {
                val leftSeat = "L$i"
                val rightSeat = "R$i"

                seats.add(
                    Seat(
                        seatNumber = leftSeat,
                        isAvailable = !bookedSeatNumbers.contains(i),
                        seatType = SeatType.SLEEPER,
                        price = bus.price + Random.nextInt(0, 200) // Slight price variation
                    )
                )

                seats.add(
                    Seat(
                        seatNumber = rightSeat,
                        isAvailable = !bookedSeatNumbers.contains(i + 12),
                        seatType = SeatType.SLEEPER,
                        price = bus.price + Random.nextInt(0, 200)
                    )
                )
            }
        } else {
            // Seater bus layout: 1-47
            for (i in 1..totalSeats) {
                seats.add(
                    Seat(
                        seatNumber = i.toString(),
                        isAvailable = !bookedSeatNumbers.contains(i),
                        seatType = SeatType.SEATER,
                        price = bus.price + Random.nextInt(-50, 100) // Price variation
                    )
                )
            }
        }

        return seats
    }
}
