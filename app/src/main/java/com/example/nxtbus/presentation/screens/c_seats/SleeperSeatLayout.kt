package com.example.nxtbus.presentation.screens.c_seats

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nxtbus.R
import com.example.nxtbus.data.a_model.Seat
import com.example.nxtbus.data.a_model.SeatType
import com.example.nxtbus.presentation.components.SeatItem
import com.example.nxtbus.presentation.theme.NxtBusTheme

@Composable
fun SleeperSeatLayout(
    seats: List<Seat>,
    onSeatClick: (Seat) -> Unit,
    modifier: Modifier = Modifier
) {
    // Robustly detect deck from ID prefixes
    fun deckOf(seatId: String): String? {
        if (seatId.isEmpty()) return null
        val id = seatId.uppercase()
        if (id.startsWith("U")) return "UPPER"
        if (id.startsWith("D")) return "LOWER"
        // For L/R: 1..6 lower, >=7 upper (as per mock and sample)
        val num = id.drop(1).takeWhile { it.isDigit() }.toIntOrNull()
        return if (id.startsWith("L") || id.startsWith("R")) {
            if ((num ?: 0) >= 7) "UPPER" else "LOWER"
        } else null
    }

    val lowerDeckSeats = seats.filter { deckOf(it.seatNumber) == "LOWER" }
    val upperDeckSeats = seats.filter { deckOf(it.seatNumber) == "UPPER" }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        DeckSection(
            title = "Lower deck",
            seats = lowerDeckSeats,
            onSeatClick = onSeatClick,
            showDriver = true
        )

        DeckSection(
            title = "Upper deck",
            seats = upperDeckSeats,
            onSeatClick = onSeatClick,
            showDriver = true
        )
    }
}

@Composable
private fun DeckSection(
    title: String,
    seats: List<Seat>,
    onSeatClick: (Seat) -> Unit,
    showDriver: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header row (title + hint for lower deck like the mock)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showDriver) {
                Text(
                    text = "Tap on a seat to select",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }

        // Outer surface to visually group seats (subtle, since parent already a Card)
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            color = Color(0xFFF8FAFF)
        ) {
            Column(
                modifier = Modifier
                    .width(200.dp)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showDriver) {
                    DriverIndicator(deckTitle = title)
                    Spacer(modifier = Modifier.height(6.dp))
                }
                SeatGrid(seats = seats, onSeatClick = onSeatClick)
            }
        }
    }
}

@Composable
private fun DriverIndicator(deckTitle: String = "Lower deck") {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = deckTitle,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
            color = Color.Black,
            modifier = Modifier.padding(start = 8.dp)
        )
        Spacer(modifier = Modifier.width(54.dp))
        Icon(
            painter = painterResource(id = R.drawable.steeringicon),
            contentDescription = "Driver",
            tint = Color.Gray,
            modifier = Modifier.size(50.dp)
        )
    }
}

@Composable
private fun SeatGrid(
    seats: List<Seat>,
    onSeatClick: (Seat) -> Unit
) {
    // Reduced gaps for a denser grid
    val betweenRightPair = 6.dp
    val aisleGap = 48.dp
    val verticalGap = 6.dp

    fun parseNumber(id: String): Int = id.drop(1).toIntOrNull() ?: Int.MAX_VALUE
    fun placeholder(id: String) = Seat(seatNumber = id, isAvailable = false, isSelected = false, seatType = SeatType.SLEEPER, price = 0)

    val leftSeats = seats.filter { it.seatNumber.uppercase().startsWith("L") }.sortedBy { parseNumber(it.seatNumber) }
    val rightSeats = seats.filter { it.seatNumber.uppercase().startsWith("R") }.sortedBy { parseNumber(it.seatNumber) }

    var lIndex = 0
    var rIndex = 0

    Column(verticalArrangement = Arrangement.spacedBy(verticalGap)) {
        repeat(6) { row ->
            val rowNumber = row + 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val leftSeat = if (lIndex < leftSeats.size) leftSeats[lIndex++] else placeholder("L$rowNumber")
                SeatItem(seat = leftSeat, onClick = { if (leftSeat.isAvailable) onSeatClick(leftSeat) })

                Spacer(modifier = Modifier.width(aisleGap))

                Row(horizontalArrangement = Arrangement.spacedBy(betweenRightPair)) {
                    val rightSeat1 = if (rIndex < rightSeats.size) rightSeats[rIndex++] else placeholder("R${rowNumber * 2 - 1}")
                    SeatItem(seat = rightSeat1, onClick = { if (rightSeat1.isAvailable) onSeatClick(rightSeat1) })

                    val rightSeat2 = if (rIndex < rightSeats.size) rightSeats[rIndex++] else placeholder("R${rowNumber * 2}")
                    SeatItem(seat = rightSeat2, onClick = { if (rightSeat2.isAvailable) onSeatClick(rightSeat2) })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SleeperSeatLayoutPreview() {
    val sampleSeats = listOf(
        Seat(seatNumber = "L1", isAvailable = true, seatType = SeatType.SLEEPER, price = 800),
        Seat(seatNumber = "L3", isAvailable = true, isSelected = true, seatType = SeatType.SLEEPER, price = 850),
        Seat(seatNumber = "L5", isAvailable = false, seatType = SeatType.SLEEPER, price = 800),
        Seat(seatNumber = "L7", isAvailable = true, seatType = SeatType.SLEEPER, price = 1100),
        Seat(seatNumber = "L9", isAvailable = true, seatType = SeatType.SLEEPER, price = 1100),
        Seat(seatNumber = "L11", isAvailable = true, seatType = SeatType.SLEEPER, price = 1100),
        Seat(seatNumber = "R1", isAvailable = true, seatType = SeatType.SLEEPER, price = 800),
        Seat(seatNumber = "R2", isAvailable = false, seatType = SeatType.SLEEPER, price = 800),
        Seat(seatNumber = "R3", isAvailable = true, seatType = SeatType.SLEEPER, price = 800),
        Seat(seatNumber = "R4", isAvailable = true, seatType = SeatType.SLEEPER, price = 800),
        Seat(seatNumber = "R5", isAvailable = true, seatType = SeatType.SLEEPER, price = 800),
        Seat(seatNumber = "R6", isAvailable = false, seatType = SeatType.SLEEPER, price = 800),
        Seat(seatNumber = "R7", isAvailable = true, seatType = SeatType.SLEEPER, price = 1100),
        Seat(seatNumber = "R8", isAvailable = true, seatType = SeatType.SLEEPER, price = 1100),
        Seat(seatNumber = "R9", isAvailable = true, seatType = SeatType.SLEEPER, price = 1100),
        Seat(seatNumber = "R10", isAvailable = false, seatType = SeatType.SLEEPER, price = 1100),
        Seat(seatNumber = "R11", isAvailable = true, seatType = SeatType.SLEEPER, price = 1100),
        Seat(seatNumber = "R12", isAvailable = true, seatType = SeatType.SLEEPER, price = 1100)
    )

    NxtBusTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            SleeperSeatLayout(
                seats = sampleSeats,
                onSeatClick = {}
            )
        }
    }
}
