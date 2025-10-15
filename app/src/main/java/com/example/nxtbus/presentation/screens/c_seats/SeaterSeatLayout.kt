package com.example.nxtbus.presentation.screens.c_seats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nxtbus.R
import com.example.nxtbus.data.a_model.Seat
import com.example.nxtbus.presentation.components.SeatItem
import com.example.nxtbus.presentation.theme.NxtBusTheme

@Composable
fun SeaterSeatLayout(
    seats: List<Seat>,
    onSeatClick: (Seat) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(220.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Driver section indicator
            Row(
                modifier = Modifier
                    .width(200.dp)
                    .height(42.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.steeringicon),
                    contentDescription = "Driver",
                    tint = Color.Gray,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Seater layout - 2+2 configuration with a dedicated 5-seat back row
            Column(
                modifier = Modifier.width(200.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val total = seats.size
                val backRowCount = if (total >= 5) 5 else total
                val normalCount = total - backRowCount

                var index = 0

                // Standard 2+2 rows until only the last 5 seats remain
                while (index < normalCount) {
                    Row(
                        modifier = Modifier.width(200.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left pair
                        repeat(2) {
                            if (index < normalCount) {
                                val currentIndex = index
                                SeatItem(
                                    seat = seats[currentIndex],
                                    onClick = { onSeatClick(seats[currentIndex]) }
                                )
                                index++
                            }
                        }

                        // Aisle space
                        Spacer(modifier = Modifier.width(16.dp))

                        // Right pair
                        repeat(2) {
                            if (index < normalCount) {
                                val currentIndex = index
                                SeatItem(
                                    seat = seats[currentIndex],
                                    onClick = { onSeatClick(seats[currentIndex]) }
                                )
                                index++
                            }
                        }
                    }
                }

                // Back row: evenly spaced seats without aisle (5 when possible)
                if (backRowCount > 0) {
                    Row(
                        modifier = Modifier.width(200.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(backRowCount) {
                            val currentIndex = index
                            SeatItem(
                                seat = seats[currentIndex],
                                onClick = { onSeatClick(seats[currentIndex]) }
                            )
                            index++
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Seater layout", showBackground = true)
@Composable
fun SeaterSeatLayoutPreview() {
    // Example: 45 seats with a 5-seat back row reserved
    val sampleSeats = (1..45).map { idx ->
        val isBooked = idx % 5 == 0
        val isSelected = !isBooked && idx % 11 == 0
        Seat(
            seatNumber = idx.toString(),
            isAvailable = !isBooked,
            isSelected = isSelected,
            price = 600 + (idx % 4) * 20
        )
    }

    NxtBusTheme {
        Surface(color = Color.White) {
            SeaterSeatLayout(
                seats = sampleSeats,
                onSeatClick = { /* no-op for preview */ },
                modifier = Modifier
                    .width(260.dp)
                    .padding(16.dp)
            )
        }
    }
}
