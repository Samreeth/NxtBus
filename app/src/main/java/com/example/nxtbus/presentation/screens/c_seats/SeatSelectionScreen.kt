package com.example.nxtbus.presentation.screens.c_seats


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.nxtbus.data.a_model.SeatType
import com.example.nxtbus.data.a_model.Seat
import com.example.nxtbus.data.a_model.Bus
import com.example.nxtbus.presentation.components.BottomNavigationBar
import com.example.nxtbus.presentation.components.ErrorMessage
import com.example.nxtbus.presentation.components.LoadingIndicator
import com.example.nxtbus.presentation.navigation.Screen
import com.example.nxtbus.presentation.theme.*
import com.example.nxtbus.utils.PriceUtils
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.nxtbus.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatSelectionScreen(
    navController: NavController,
    viewModel: SeatSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Select Seats",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Small placeholder text/icon on top-right (e.g., steering/stairs icon)
                    Text(
                        text = "Stee",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
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
                    message = "Loading seats...",
                    modifier = Modifier.fillMaxSize()
                )
            }

            uiState.errorMessage.isNotEmpty() -> {
                ErrorMessage(
                    message = uiState.errorMessage,
                    onRetry = { /* Retry logic */ },
                    modifier = Modifier.fillMaxSize()
                )
            }

            uiState.bus != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Tab selector (only show if bus has both types)
                    if (uiState.bus!!.busType.contains("Sleeper")) {
                        TabRow(
                            selectedTabIndex = uiState.selectedTab.ordinal,
                            containerColor = Color.White,
                            contentColor = PrimaryBlue
                        ) {
                            Tab(
                                selected = uiState.selectedTab == SeatTab.SLEEPER,
                                onClick = { viewModel.selectTab(SeatTab.SLEEPER) },
                                text = { Text("Sleeper") }
                            )
                            Tab(
                                selected = uiState.selectedTab == SeatTab.SEATER,
                                onClick = { viewModel.selectTab(SeatTab.SEATER) },
                                text = { Text("Seater") }
                            )
                        }
                    }

                    // Seat layout
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(12.dp)
                        ) {
                            // Bus info
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Text(
                                    text = "${uiState.bus!!.operatorName} - ${uiState.bus!!.busType}",
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Seat layout based on bus type and selected tab
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    val isSleeperBus = uiState.bus!!.busType.contains("Sleeper")
                                    when {
                                        isSleeperBus && uiState.selectedTab == SeatTab.SLEEPER -> {
                                            SleeperSeatLayout(
                                                seats = uiState.seats.filter { it.seatType == SeatType.SLEEPER },
                                                onSeatClick = { seat -> viewModel.toggleSeatSelection(seat) }
                                            )
                                        }
                                        isSleeperBus && uiState.selectedTab == SeatTab.SEATER -> {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 24.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "Seater chart is not avalilable for this Bus",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = Color.Gray
                                                )
                                            }
                                        }
                                        else -> {
                                            SeaterSeatLayout(
                                                seats = uiState.seats.filter { it.seatType == SeatType.SEATER },
                                                onSeatClick = { seat -> viewModel.toggleSeatSelection(seat) }
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Legend
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(
                                        text = "Legend",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 10.dp)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        LegendItem(
                                            color = SeatAvailable,
                                            label = "Available"
                                        )
                                        LegendItem(
                                            color = SeatBooked,
                                            label = "Booked"
                                        )
                                        LegendItem(
                                            color = SeatSelected,
                                            label = "Selected"
                                        )
                                    }
                                }
                            }

                            // Amenities section below Legend
                            uiState.bus?.let { bus ->
                                if (bus.amenities.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    AmenitiesSection(amenities = bus.amenities)
                                    // Image under the amenities card
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Image(
                                        painter = painterResource(id = R.drawable.nxt_assured_card),
                                        contentDescription = "Decorative",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(2.3f),
                                        contentScale = ContentScale.FillWidth
                                    )
                                    // Travel policy card under the image
                                    Spacer(modifier = Modifier.height(12.dp))
                                    TravelPolicyCard()
                                }
                            }

                            Spacer(modifier = Modifier.height(84.dp)) // Space for bottom section
                        }
                    }

                    // Bottom selection summary redesigned as persistent bar
                    Surface(
                        color = Color.White,
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Total Price",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                                Text(
                                    text = PriceUtils.formatPrice(uiState.totalAmount),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                            }
                            Button(
                                onClick = {
                                    if (uiState.selectedSeats.isNotEmpty()) {
                                        val selectedSeatsString = uiState.selectedSeats.joinToString(",") { it.seatNumber }
                                        navController.navigate(
                                            Screen.PassengerDetails.createRoute(
                                                uiState.bus!!.id,
                                                selectedSeatsString,
                                                uiState.totalAmount,
                                                uiState.fromCity,
                                                uiState.toCity,
                                                uiState.selectedDate
                                            )
                                        )
                                    }
                                },
                                enabled = uiState.selectedSeats.isNotEmpty(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryBlue,
                                    disabledContainerColor = PrimaryBlue.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .height(52.dp)
                            ) {
                                Text(
                                    text = "Continue",
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
    }
}

@Composable
private fun AmenitiesSection(amenities: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Amenities",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            AmenitiesGrid(amenities)
        }
    }
}

@Composable
private fun AmenitiesGrid(amenities: List<String>) {
    val rows = amenities.chunked(4)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { amenity ->
                    Box(modifier = Modifier.weight(1f)) {
                        AmenityTile(
                            icon = amenityToIcon(amenity),
                            label = amenity
                        )
                    }
                }
                // fill empty spaces if row < 4 to keep spacing
                repeat(4 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun AmenityTile(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            color = Color(0xFFF8FAFF),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color(0xFF5B5B5B)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF4A4A4A)
        )
    }
}

@Composable
private fun amenityToIcon(name: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (name.trim().lowercase()) {
        "charging point", "charging", "charger", "usb", "power" -> Icons.Filled.Power
        "water bottle", "water", "bottle" -> Icons.Filled.WaterDrop
        "reading light", "reading", "light", "lamp" -> Icons.Filled.AutoStories
        "facial tissues", "tissues", "tissue" -> Icons.Filled.Inventory2
        "ac", "a/c", "air conditioning", "air-condition" -> Icons.Filled.AcUnit
        "wifi", "wi-fi", "internet" -> Icons.Filled.Wifi
        "blanket" -> Icons.Filled.Bedtime
        "tv", "television" -> Icons.Filled.Tv
        else -> Icons.Filled.Checkroom // generic fallback icon
    }
}

// New: Travel Policy card under nxt_assured_card image
@Composable
private fun TravelPolicyCard() {
    val questionStyle = MaterialTheme.typography.titleSmall.copy(
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF111827)
    )
    val answerStyle = MaterialTheme.typography.bodySmall.copy(
        color = Color(0xFF6B7280)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Travel Policy",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(12.dp))

            QAItem(
                question = "Do I need to book a ticket for my child?",
                answer = "Children above 5 years of age need a ticket. There is no charge for children below 5 years, provided they don't need a separate seat.",
                questionStyle = questionStyle,
                answerStyle = answerStyle
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE5E7EB))

            QAItem(
                question = "What is the policy for excess luggage?",
                answer = "Passengers can carry luggage up to 15 kg. For any excess luggage, the operator may charge an additional fee.",
                questionStyle = questionStyle,
                answerStyle = answerStyle
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE5E7EB))

            QAItem(
                question = "Are pets allowed on the bus?",
                answer = "Pets are generally not allowed on board. Please check with the bus operator for their specific policy on pet travel.",
                questionStyle = questionStyle,
                answerStyle = answerStyle
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE5E7EB))

            QAItem(
                question = "Is consumption of alcohol permitted?",
                answer = "Consumption of alcohol or any intoxicating substances is strictly prohibited on the bus.",
                questionStyle = questionStyle,
                answerStyle = answerStyle
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE5E7EB))

            QAItem(
                question = "What happens if I'm late for boarding?",
                answer = "It is recommended to arrive at the boarding point 15 minutes prior to the departure time. The bus will not wait for late passengers.",
                questionStyle = questionStyle,
                answerStyle = answerStyle
            )
        }
    }
}

@Composable
private fun QAItem(
    question: String,
    answer: String,
    questionStyle: androidx.compose.ui.text.TextStyle,
    answerStyle: androidx.compose.ui.text.TextStyle
) {
    Column {
        Text(text = question, style = questionStyle)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = answer, style = answerStyle)
    }
}

// Preview-only UI without Hilt or repository dependencies
@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Seat Selection - Sleeper", showBackground = true, showSystemUi = true)
@Composable
private fun SeatSelectionScreenPreview() {
    val navController = rememberNavController()

    // Sample bus and seats
    val sampleBus = Bus(
        id = "BUS999",
        operatorName = "NxtBus Demo",
        busType = "A/C Sleeper",
        departureTime = "21:00",
        arrivalTime = "06:00",
        duration = "9h 0m",
        distance = "500km",
        price = 800,
        availableSeats = 16,
        totalSeats = 24,
        amenities = listOf("AC", "Blanket"),
        rating = 4.5f
    )

    // Build a simple sample sleeper set: L1..L12, R1..R12
    val lowerPrices = 800
    val upperPrices = 1100

    fun sleeperSeat(id: String, available: Boolean, selected: Boolean, price: Int) =
        Seat(seatNumber = id, isAvailable = available, isSelected = selected, seatType = SeatType.SLEEPER, price = price)

    val sampleSeats = buildList {
        // Lower deck 1..6
        addAll(
            listOf(
                sleeperSeat("L1", true, false, lowerPrices),
                sleeperSeat("R1", true, false, lowerPrices),
                sleeperSeat("R2", true, false, lowerPrices),
                sleeperSeat("L2", false, false, lowerPrices),
                sleeperSeat("R3", true, false, lowerPrices),
                sleeperSeat("R4", false, false, lowerPrices),
                sleeperSeat("L3", true, true, lowerPrices), // selected to show blue state
                sleeperSeat("R5", true, false, lowerPrices),
                sleeperSeat("R6", true, false, lowerPrices),
                sleeperSeat("L4", false, false, lowerPrices),
                sleeperSeat("L5", false, false, lowerPrices),
                sleeperSeat("L6", true, false, lowerPrices)
            )
        )
        // Upper deck 7..12
        addAll(
            listOf(
                sleeperSeat("L7", true, false, upperPrices),
                sleeperSeat("R7", false, false, upperPrices),
                sleeperSeat("R8", true, false, upperPrices),
                sleeperSeat("L8", false, false, upperPrices),
                sleeperSeat("R9", true, false, upperPrices),
                sleeperSeat("R10", false, false, upperPrices),
                sleeperSeat("L9", true, false, upperPrices),
                sleeperSeat("R11", true, false, upperPrices),
                sleeperSeat("R12", false, false, upperPrices),
                sleeperSeat("L10", true, false, upperPrices),
                sleeperSeat("L11", true, false, upperPrices),
                sleeperSeat("L12", false, false, upperPrices)
            )
        )
    }

    val selectedSeats = sampleSeats.filter { it.isSelected }
    val uiState = SeatSelectionUiState(
        bus = sampleBus,
        seats = sampleSeats,
        selectedSeats = selectedSeats,
        selectedTab = SeatTab.SLEEPER,
        totalAmount = selectedSeats.sumOf { it.price }
    )

    NxtBusTheme {
        // Inline version of the UI using the preview uiState
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Select Seats",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { /* no-op */ }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        Spacer(modifier = Modifier.width(12.dp))
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                TabRow(
                    selectedTabIndex = uiState.selectedTab.ordinal,
                    containerColor = Color.White,
                    contentColor = PrimaryBlue
                ) {
                    Tab(selected = true, onClick = { }, text = { Text("Sleeper") })
                    Tab(selected = false, onClick = { }, text = { Text("Seater") })
                }

                Box(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Text(
                                text = "${sampleBus.operatorName} - ${sampleBus.busType}",
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            modifier = Modifier
                                .height(480.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                SleeperSeatLayout(
                                    seats = uiState.seats.filter { it.seatType == SeatType.SLEEPER },
                                    onSeatClick = { }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Legend",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    LegendItem(color = SeatAvailable, label = "Available")
                                    LegendItem(color = SeatBooked, label = "Booked")
                                    LegendItem(color = SeatSelected, label = "Selected")
                                }
                            }
                        }

                        // Amenities section below Legend (preview)
                        uiState.bus?.let { bus ->
                            if (bus.amenities.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                AmenitiesSection(amenities = bus.amenities)
                                Spacer(modifier = Modifier.height(12.dp))
                                Image(
                                    painter = painterResource(id = R.drawable.nxt_assured_badge),
                                    contentDescription = "Decorative",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(2.3f),
                                    contentScale = ContentScale.FillWidth
                                )
                                // Travel policy card under the image (preview)
                                Spacer(modifier = Modifier.height(12.dp))
                                TravelPolicyCard()
                            }
                        }

                        Spacer(modifier = Modifier.height(84.dp))
                    }
                }

                Surface(color = Color.White, shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Total Price",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            Text(
                                text = PriceUtils.formatPrice(uiState.totalAmount),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }
                        Button(
                            onClick = { },
                            enabled = uiState.selectedSeats.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue,
                                disabledContainerColor = PrimaryBlue.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Text(
                                text = "Continue",
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
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}