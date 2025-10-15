package com.example.nxtbus.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nxtbus.data.a_model.Seat
import com.example.nxtbus.data.a_model.SeatType
import com.example.nxtbus.presentation.theme.*
import com.example.nxtbus.utils.PriceUtils

@Preview(showBackground = true)
@Composable
fun SeatItemAvailablePreview() {
    NxtBusTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            SeatItem(
                seat = Seat(
                    seatNumber = "S1",
                    isAvailable = true,
                    isSelected = false,
                    seatType = SeatType.SEATER,
                    price = 269
                ),
                onClick = {}
            )

            SeatItem(
                seat = Seat(
                    seatNumber = "S2",
                    isAvailable = true,
                    isSelected = true,
                    seatType = SeatType.SEATER,
                    price = 269
                ),
                onClick = {}
            )

            SeatItem(
                seat = Seat(
                    seatNumber = "S3",
                    isAvailable = false,
                    isSelected = false,
                    seatType = SeatType.SEATER,
                    price = 269
                ),
                onClick = {}
            )
        }
    }
}

@Composable
fun SeatItem(
    seat: Seat,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (seat.seatType == SeatType.SLEEPER) {
        // Sleeper style: border-only for available, blue border for selected, gray fill for booked.
        val bgColor = when {
            !seat.isAvailable -> Color(0xFFE0E0E0)
            seat.isSelected -> Color(0xFFE9F2FF) // pale blue
            else -> Color(0xFFEFF9F1) // pale green
        }
        val borderColor = when {
            !seat.isAvailable -> Color.Transparent
            seat.isSelected -> PrimaryBlue
            else -> SeatAvailable
        }
        val priceColor = when {
            !seat.isAvailable -> Color(0xFF757575)
            seat.isSelected -> PrimaryBlue
            else -> SeatAvailable
        }

        Box(
            modifier = modifier
                .size(width = 36.dp, height = 64.dp)
                .background(color = bgColor, shape = RoundedCornerShape(10.dp))
                .border(
                    width = if (seat.isAvailable) if (seat.isSelected) 2.dp else 1.dp else 0.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(5.dp)
                )
                .clickable(enabled = seat.isAvailable) { onClick() }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(6.dp))
                if (seat.isAvailable) {
                    Text(
                        text = PriceUtils.formatPrice(seat.price),
                        color = priceColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(width = 22.dp, height = 5.dp)
                        .background(
                            color = when {
                                !seat.isAvailable -> Color(0xFFBDBDBD)
                                seat.isSelected -> PrimaryBlue
                                else -> SeatAvailable
                            },
                            shape = RoundedCornerShape(3.dp)
                        )
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    } else {
        // SEATER style aligned with sleeper palette
        val bgColor = Color(0xFFE0E0E0)
        val borderColor = when {
            !seat.isAvailable -> Color.Transparent
            seat.isSelected -> PrimaryBlue
            else -> SeatAvailable
        }
        val priceColor = when {
            !seat.isAvailable -> Color.Transparent
            seat.isSelected -> PrimaryBlue
            else -> SeatAvailable
        }
        val notchColor = when {
            !seat.isAvailable -> Color(0xFFBDBDBD)
            seat.isSelected -> PrimaryBlue
            else -> SeatAvailable
        }

        Box(
            modifier = modifier
                .size(width = 30.dp, height = 30.dp)
                .background(color = bgColor, shape = RoundedCornerShape(6.dp))
                .border(
                    width = if (seat.isAvailable) if (seat.isSelected) 2.dp else 1.dp else 0.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(4.dp)
                )
                .clickable(enabled = seat.isAvailable) { onClick() }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(2.dp))
                if (seat.isAvailable) {
                    Text(
                        text = PriceUtils.formatPrice(seat.price),
                        color = priceColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .size(width = 14.dp, height = 3.dp)
                        .background(notchColor, shape = RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}
