package com.example.nxtbus.presentation.screens.e_confirmation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.nxtbus.presentation.components.BottomNavigationBar
import com.example.nxtbus.presentation.components.ErrorMessage
import com.example.nxtbus.presentation.components.LoadingIndicator
import com.example.nxtbus.presentation.navigation.Screen
import com.example.nxtbus.presentation.theme.PrimaryBlue
import com.example.nxtbus.utils.PriceUtils
import com.example.nxtbus.data.a_model.Booking
import kotlin.math.hypot
import androidx.compose.animation.core.animateFloatAsState
import com.example.nxtbus.presentation.theme.redColor
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.zIndex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingConfirmationScreen(
    navController: NavController,
    viewModel: BookingConfirmationViewModel = hiltViewModel()
) {
    // Use global theme typography (Plus Jakarta Sans applied app-wide)
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var isCancelling by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

    // Collect UI state from ViewModel (fixes unresolved reference)
    val uiState by viewModel.uiState.collectAsState()

    // Wrap Scaffold so our overlay can sit above everything (including bottomBar)
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Booking Confirmed",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PrimaryBlue
                    )
                )
            },
            bottomBar = {
                BottomNavigationBar(navController = navController)
            }
        ) { paddingValues ->
            when {
                uiState.isLoading -> {
                    LoadingIndicator(
                        message = "Loading booking details...",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                uiState.errorMessage.isNotEmpty() -> {
                    ErrorMessage(
                        message = uiState.errorMessage,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                uiState.booking != null -> {
                    val booking = uiState.booking!!

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        Column(
                            modifier = Modifier
                                .matchParentSize()
                                .verticalScroll(scrollState)
                                .padding(16.dp)
                                .padding(bottom = 120.dp), // ensure content above fixed bottom CTA
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Styled header like the mock
                            ThankYouHeader()

                            // Booking (ticket) card
                            TicketCard(booking = booking)

                            // Rounded, subtle download button
                            DownloadTicketButton(onClick = { /* TODO: download */ })

                            // Cancel ticket button with same styling
                            CancelTicketButton(
                                enabled = !isCancelling,
                                onClick = { if (!isCancelling) showCancelDialog = true }
                            )

                            // Dedicated support card
                            SupportCard()
                        }

                        // View Ticket Button
                        Surface(
                            color = Color.White,
                            shadowElevation = 8.dp,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        ) {
                            Button(
                                onClick = { navController.navigate(Screen.MyTickets.route) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryBlue
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(
                                    text = "View Ticket",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Global overlay above Scaffold (fixes being hidden by bottomBar on some devices)
        if (showCancelDialog) {
            val interaction = remember { MutableInteractionSource() }
            // Scrim
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable(
                        interactionSource = interaction,
                        indication = null,
                        enabled = !isCancelling
                    ) { showCancelDialog = false }
                    .zIndex(1f)
            )
            // Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .align(Alignment.Center)
                    .zIndex(2f)
            ) {
                CancelConfirmationCard(
                    onDismiss = { if (!isCancelling) showCancelDialog = false },
                    onConfirm = {
                        if (!isCancelling) {
                            isCancelling = true
                            scope.launch {
                                val success = viewModel.cancelBooking()
                                isCancelling = false
                                if (success) {
                                    showCancelDialog = false
                                    val pnr = uiState.booking?.pnr ?: return@launch
                                    navController.navigate(Screen.TicketCancelled.createRoute(pnr))
                                }
                            }
                        }
                    },
                    isCancelling = isCancelling
                )
            }
        }
    }
}

@Composable
private fun ThankYouHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SuccessCheckAnimation(diameter = 120.dp)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Thank You !",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF5C3B1E) /* keep color family; adjust if needed */
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Your exclusive journey awaits.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
    }
}

@Composable
private fun SuccessCheckAnimation(
    diameter: Dp = 120.dp,
    circleColor: Color = PrimaryBlue,
    checkColor: Color = Color(0xFF2E7D32)
) {
    val scale = remember { Animatable(0.6f) }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Pop-in circle
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
        )
        // Draw check
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 700)
        )
    }

    Box(
        modifier = Modifier
            .size(diameter)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val minDim = size.minDimension
            // Outer circle
            drawCircle(
                color = circleColor.copy(alpha = 0.12f),
                radius = minDim / 2f
            )
            // Middle circle
            drawCircle(
                color = circleColor.copy(alpha = 0.18f),
                radius = minDim / 2.6f
            )
            // Solid inner circle
            drawCircle(
                color = circleColor,
                radius = minDim / 3.2f
            )

            // Animated checkmark
            val w = size.width
            val h = size.height
            val start = Offset(x = w * 0.32f, y = h * 0.54f)
            val mid = Offset(x = w * 0.46f, y = h * 0.68f)
            val end = Offset(x = w * 0.72f, y = h * 0.40f)

            val len1 = hypot((mid.x - start.x).toDouble(), (mid.y - start.y).toDouble()).toFloat()
            val len2 = hypot((end.x - mid.x).toDouble(), (end.y - mid.y).toDouble()).toFloat()
            val total = len1 + len2
            val drawLen = total * progress.value

            // Draw first segment (start -> mid)
            if (drawLen > 0f) {
                val seg1 = drawLen.coerceAtMost(len1)
                val t1 = if (len1 > 0f) seg1 / len1 else 1f
                val p1 = Offset(
                    x = start.x + (mid.x - start.x) * t1,
                    y = start.y + (mid.y - start.y) * t1
                )
                drawLine(
                    color = checkColor,
                    start = start,
                    end = p1,
                    strokeWidth = 8f,
                    cap = StrokeCap.Round
                )
            }
            // Draw second segment (mid -> end)
            if (drawLen > len1) {
                val seg2 = (drawLen - len1).coerceAtMost(len2)
                val t2 = if (len2 > 0f) seg2 / len2 else 1f
                val p2 = Offset(
                    x = mid.x + (end.x - mid.x) * t2,
                    y = mid.y + (end.y - mid.y) * t2
                )
                drawLine(
                    color = checkColor,
                    start = mid,
                    end = p2,
                    strokeWidth = 8f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
private fun TicketCard(booking: Booking) {
    Box(modifier = Modifier.fillMaxWidth()) {
        // Card content
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = rememberVectorPainter(Icons.Default.DirectionsBus),
                        contentDescription = "Bus",
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PNR: ${booking.pnr}",
                            style = MaterialTheme.typography.labelLarge,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${booking.fromCity} to ${booking.toCity}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        // Bus name with seats left aligned to the right
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = booking.bus.operatorName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black,
                                modifier = Modifier.weight(1f)
                            )
                            val seatsColor = when {
                                booking.bus.availableSeats < 10 -> redColor // Red
                                booking.bus.availableSeats < 15 -> Color(0xFFFFC107) // Yellow 10..14
                                else -> Color(0xFF2E7D32) // Green 15+
                            }
                            Text(
                                text = "${booking.bus.availableSeats} seats left",
                                style = MaterialTheme.typography.bodySmall,
                                color = seatsColor,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.End
                            )
                        }
                        // Amenities (replacing the old 'seats left' spot as requested)
                        if (booking.bus.amenities.isNotEmpty()) {
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = booking.bus.amenities.joinToString(" • "),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                DashedDivider()
                Spacer(Modifier.height(12.dp))

                Text(
                    text = "${booking.journeyDate} | ${booking.bus.departureTime} - ${booking.bus.arrivalTime}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(Modifier.height(16.dp))

                val passengerNames = remember(booking) {
                    val list = (booking.passengers.orEmpty()).ifEmpty { booking.passenger?.let { listOf(it) } ?: emptyList() }
                    list.joinToString(", ") { it.name }
                }
                Text(
                    text = if (passengerNames.contains(",")) "PASSENGERS" else "PASSENGER",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Text(
                    text = passengerNames.ifBlank { "-" },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(16.dp))

                // Seat details (left) and Total fare (right). Prevent vertical wrapping on the right by measuring it first
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SEAT DETAILS",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                        Text(
                            text = "Seat ${booking.selectedSeats.orEmpty().joinToString(", ")} (${booking.bus.busType})",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(
                        modifier = Modifier
                            .widthIn(min = 120.dp)
                            .wrapContentWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "TOTAL FARE",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Clip
                        )
                        Text(
                            text = PriceUtils.formatPrice(booking.totalAmount),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.End,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Paid via Razorpay",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = TextAlign.End,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Notches (detachable stub look) with subtle inner shadow and entrance animation
        TicketNotch(
            alignment = Alignment.CenterStart,
            backgroundColor = MaterialTheme.colorScheme.background,
            offsetX = (-12).dp
        )
        TicketNotch(
            alignment = Alignment.CenterEnd,
            backgroundColor = MaterialTheme.colorScheme.background,
            offsetX = 12.dp
        )
    }
}

@Composable
private fun DashedDivider(color: Color = Color(0xFFE0E0E0)) {
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)) {
        val dashWidth = 14f
        val dashGap = 10f
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2f),
            end = Offset(size.width, size.height / 2f),
            strokeWidth = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashWidth, dashGap), 0f)
        )
    }
}

@Composable
private fun DownloadTicketButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryBlue.copy(alpha = 0.08f),
            contentColor = PrimaryBlue
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Download,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text("Download Ticket", fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun CancelTicketButton(onClick: () -> Unit, enabled: Boolean = true) {
    // Same styling as DownloadTicketButton
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = redColor.copy(alpha = 0.5f),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Cancel,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text("Cancel Ticket", fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SupportCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Dedicated Support",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Need assistance? Our concierge team is here to help.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = PrimaryBlue)
                Spacer(Modifier.width(10.dp))
                Text(text = "+1(234) 567-890", style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.AccessTime, contentDescription = null, tint = PrimaryBlue)
                Spacer(Modifier.width(10.dp))
                Text(text = "Schedule a Callback", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun BoxScope.TicketNotch(
    alignment: Alignment,
    backgroundColor: Color,
    offsetX: Dp,
    notchSize: Dp = 24.dp,
    shadowAlpha: Float = 0.12f,
    animate: Boolean = true
) {
    var visible by remember { mutableStateOf(!animate) }
    LaunchedEffect(animate) { if (animate) visible = true }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = "notch-scale"
    )

    Box(
        modifier = Modifier
            .align(alignment)
            .offset(x = offsetX)
            .size(notchSize)
            .graphicsLayer { scaleX = scale; scaleY = scale }
    ) {
        // Cutout circle
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(color = backgroundColor, shape = CircleShape)
        )
        // Inner shadow ring (subtle)
        Canvas(modifier = Modifier.matchParentSize()) {
            val radius = size.minDimension / 2f
            drawCircle(
                color = Color.Black.copy(alpha = shadowAlpha),
                radius = radius * 0.96f,
                style = Stroke(width = radius * 0.18f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Booking Confirmation", showBackground = true, showSystemUi = true)
@Composable
private fun BookingConfirmationScreenPreview() {
    // Build a sample booking
    val sampleBus = com.example.nxtbus.data.a_model.Bus(
        id = "BUS123",
        operatorName = "Blue Bus",
        busType = "A/C Sleeper",
        departureTime = "21:00",
        arrivalTime = "06:00",
        duration = "9h 0m",
        distance = "500km",
        price = 1200,
        availableSeats = 3,
        totalSeats = 24,
        amenities = listOf("AC", "Blanket", "Charging"),
        rating = 4.4f
    )
    val sampleBooking = Booking(
        pnr = "PNR123456",
        bus = sampleBus,
        selectedSeats = listOf("R4", "R5"),
        passengers = listOf(
            com.example.nxtbus.data.a_model.Passenger(name = "John Doe", age = 28, gender = "Male", seatNumber = "R4"),
            com.example.nxtbus.data.a_model.Passenger(name = "Jane Doe", age = 26, gender = "Female", seatNumber = "R5")
        ),
        contactDetails = com.example.nxtbus.data.a_model.ContactDetails(
            mobileNumber = "9999999999",
            emailAddress = "john@example.com"
        ),
        journeyDate = "2025/10/20",
        fromCity = "City A",
        toCity = "City B",
        totalAmount = 12332,
        bookingDate = "2025/10/10"
    )

    val scrollState = rememberScrollState()

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Booking Confirmed",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { /* no-op in preview */ }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PrimaryBlue
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ThankYouHeader()
                    TicketCard(booking = sampleBooking)
                    DownloadTicketButton(onClick = { })
                    CancelTicketButton(onClick = { })
                    SupportCard()
                }
                Surface(color = Color.White, shadowElevation = 8.dp) {
                    Button(
                        onClick = { /* no-op */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "View Tickets",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CancelConfirmationCard(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isCancelling: Boolean
) {
    val scroll = rememberScrollState()
    Surface(
        modifier = Modifier
            .widthIn(min = 280.dp, max = 360.dp),
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 6.dp,
        shadowElevation = 12.dp,
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(scroll),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Accent icon in a subtle circular background
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .background(color = redColor.copy(alpha = 0.10f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(36.dp)
                )
            }

            Text(
                text = "Cancel this ticket?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1A1A1A)
            )
            Text(
                text = "Are you sure you want to cancel the ticket? This action cannot be undone.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val darkGreen = Color(0xFF1B5E20)
                val darkRed = Color(0xFFB71C1C)

                OutlinedButton(
                    onClick = onDismiss,
                    enabled = !isCancelling,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = darkGreen
                    ),
                    border = BorderStroke(1.dp, darkGreen)
                ) {
                    Text("No, keep ticket", color = darkGreen)
                }
                OutlinedButton(
                    onClick = onConfirm,
                    enabled = !isCancelling,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = darkRed
                    ),
                    border = BorderStroke(1.dp, darkRed)
                ) {
                    if (isCancelling) {
                        CircularProgressIndicator(
                            color = darkRed,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Cancelling...", color = darkRed)
                    } else {
                        Text("Yes, cancel", color = darkRed)
                    }
                }
            }
        }
    }
}

@Preview(name = "Cancel Dialog (Card)", showBackground = true, showSystemUi = false)
@Composable
private fun CancelConfirmationCardPreview() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CancelConfirmationCard(
                onDismiss = {},
                onConfirm = {},
                isCancelling = false
            )
        }
    }
}

@Preview(name = "Cancel Dialog (Card) - Cancelling", showBackground = true, showSystemUi = false)
@Composable
private fun CancelConfirmationCardCancellingPreview() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CancelConfirmationCard(
                onDismiss = {},
                onConfirm = {},
                isCancelling = true
            )
        }
    }
}
