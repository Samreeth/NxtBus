package com.example.nxtbus.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nxtbus.data.a_model.Bus
import com.example.nxtbus.presentation.theme.NxtBusTheme
import com.example.nxtbus.presentation.theme.PrimaryBlue
import com.example.nxtbus.utils.PriceUtils

@Preview(showBackground = true)
@Composable
fun BusCardPreview() {
    NxtBusTheme {
        BusCard(
            bus = Bus(
                id = "BUS001",
                operatorName = "Blue Bus",
                busType = "A/C Seater / Sleeper (2+1)",
                departureTime = "10:00",
                arrivalTime = "18:00",
                duration = "8h 0m",
                distance = "450 km",
                price = 899,
                availableSeats = 20,
                totalSeats = 40,
                amenities = listOf("WiFi", "AC", "Charging Point"),
                rating = 4.5f
            ),
            fromCity = "Mumbai",
            toCity = "Pune",
            onClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusCard(
    bus: Bus,
    fromCity: String,
    toCity: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE6EAF2))),
        onClick = onClick
    ) {
        Column {
            // Header: light blue band with operator + bus type and seats-left on the right
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFFE9F2FF))
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = bus.operatorName,
                            style = MaterialTheme.typography.titleMedium,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = bus.busType,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF6B7A90)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    val seatsColor = when {
                        bus.availableSeats < 10 -> Color(0xFFD32F2F) // Red
                        bus.availableSeats < 15 -> Color(0xFFFF9800) // Yellow for 10..14
                        else -> Color(0xFF2E7D32) // Green 15+
                    }
                    Text(
                        text = "${bus.availableSeats} seats left",
                        style = MaterialTheme.typography.bodySmall,
                        color = seatsColor,
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                // Journey details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Departure
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = bus.departureTime,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = fromCity,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    // Duration and distance
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = bus.duration,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6B7A90)
                        )
                        HorizontalDivider(
                            modifier = Modifier
                                .width(80.dp)
                                .padding(vertical = 4.dp),
                            color = PrimaryBlue.copy(alpha = 0.35f)
                        )
                        Text(
                            text = bus.distance,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF6B7A90)
                        )
                    }

                    // Arrival
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = bus.arrivalTime,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = toCity,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 10.dp),
                    color = Color(0xFFE6EAF2)
                )

                // Bottom: Price on left, amenities on the right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Starts from",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Text(
                            text = PriceUtils.formatPrice(bus.price),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue,
                            fontSize = 24.sp
                        )
                    }

                    AmenitiesChips(
                        amenities = bus.amenities,
                        modifier = Modifier
                            .wrapContentWidth()
                            .horizontalScroll(rememberScrollState())
                    )
                }
            }
        }
    }
}

@Composable
private fun AmenitiesChips(amenities: List<String>, modifier: Modifier = Modifier) {
    if (amenities.isEmpty()) return
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        amenities.take(4).forEach { amenity ->
            Surface(
                color = Color(0xFFF4F7FB),
                shape = RoundedCornerShape(8.dp),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
                border = CardDefaults.outlinedCardBorder().copy(width = 0.dp)
            ) {
                Text(
                    text = amenity,
                    color = Color(0xFF6B7A90),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
        if (amenities.size > 4) {
            Text(
                text = "+${amenities.size - 4}",
                color = Color(0xFF6B7A90),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}