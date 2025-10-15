package com.example.nxtbus.presentation.screens.d_passenger


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Female
import androidx.compose.material.icons.outlined.Male
import androidx.compose.material.icons.outlined.Transgender
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.nxtbus.presentation.components.BottomNavigationBar
import com.example.nxtbus.presentation.components.CustomTextField
import com.example.nxtbus.presentation.components.LoadingIndicator
import com.example.nxtbus.presentation.navigation.Screen
import com.example.nxtbus.presentation.theme.PrimaryBlue
import com.example.nxtbus.presentation.payment.PaymentActivity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.nxtbus.data.a_model.Bus
import com.example.nxtbus.R
import androidx.compose.ui.graphics.Brush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassengerDetailsScreen(
    navController: NavController,
    viewModel: PassengerDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    // Removed unused scrollState
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val paymentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            // Payment success -> proceed to booking
            viewModel.proceedToPayment { pnr ->
                navController.navigate(Screen.BookingConfirmation.createRoute(pnr)) {
                    popUpTo(Screen.Home.route) { inclusive = false }
                }
            }
        } else {
            val msg = result.data?.getStringExtra(PaymentActivity.RESULT_ERROR)
                ?: "Payment cancelled"
            scope.launch { snackbarHostState.showSnackbar(msg) }
        }
    }

    if (uiState.isBooking) {
        LoadingIndicator(
            message = "Processing booking...",
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    PassengerDetailsContent(
        uiState = uiState,
        onUpdatePassengerName = viewModel::updatePassengerName,
        onUpdatePassengerAge = viewModel::updatePassengerAge,
        onUpdatePassengerGender = viewModel::updatePassengerGender,
        onUpdateMobile = viewModel::updateMobileNumber,
        onUpdateEmail = viewModel::updateEmailAddress,
        onProceed = {
            if (viewModel.validateForm()) {
                val intent = Intent(context, PaymentActivity::class.java).apply {
                    putExtra(PaymentActivity.EXTRA_AMOUNT_PAISE, uiState.totalAmount.toLong() * 100L)
                    putExtra(PaymentActivity.EXTRA_CURRENCY, "INR")
                    putExtra(PaymentActivity.EXTRA_NAME, "NxtBus")
                    val desc = buildString {
                        append("Booking for ")
                        if (uiState.bus?.operatorName != null) {
                            append(uiState.bus!!.operatorName)
                            append(" ")
                        }
                        append("Seat(s) ")
                        append(uiState.selectedSeats.joinToString(","))
                    }
                    putExtra(PaymentActivity.EXTRA_DESCRIPTION, desc)
                    putExtra(PaymentActivity.EXTRA_CONTACT, uiState.mobileNumber)
                    putExtra(PaymentActivity.EXTRA_EMAIL, uiState.emailAddress)
                }
                paymentLauncher.launch(intent)
            }
        },
        onNavigateUp = { navController.navigateUp() },
        navController = navController,
        snackbarHostState = snackbarHostState,
        onRefundSelected = viewModel::setRefundSelected,
        onAckoSelected = viewModel::setAckoSelected
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PassengerDetailsContent(
    uiState: PassengerDetailsUiState,
    onUpdatePassengerName: (Int, String) -> Unit,
    onUpdatePassengerAge: (Int, String) -> Unit,
    onUpdatePassengerGender: (Int, String) -> Unit,
    onUpdateMobile: (String) -> Unit,
    onUpdateEmail: (String) -> Unit,
    onProceed: () -> Unit,
    onNavigateUp: () -> Unit,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    onRefundSelected: (Boolean) -> Unit,
    onAckoSelected: (Boolean) -> Unit
) {
    val scrollState = rememberScrollState()

    // Local UI states
    val accentPurple = Color(0xFFA855F7)
    val primaryBlue = PrimaryBlue
    val greenBannerBg = Color(0xFFD1FAE5)
    val greenBannerText = Color(0xFF166534)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Passenger Details",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color(0xFFF6F7F8)
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
                // Render one section per passenger/seat
                uiState.passengers.forEachIndexed { index, passenger ->
                    // Passenger details card (compact)
                    ElevatedCard(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Passenger ${index + 1}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            // Name
                            CustomTextField(
                                value = passenger.name,
                                onValueChange = { onUpdatePassengerName(index, it) },
                                label = "Name",
                                placeholder = "Enter your name",
                                leadingIcon = Icons.Default.Person,
                                isError = passenger.nameError.isNotEmpty(),
                                errorMessage = passenger.nameError
                            )

                            // Gender selection grid (compact)
                            Column {
                                Text(
                                    text = "Gender",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF6B7280)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    GenderOption(
                                        label = "Male",
                                        icon = Icons.Outlined.Male,
                                        selected = passenger.gender == "Male",
                                        accent = accentPurple,
                                        onClick = { onUpdatePassengerGender(index, "Male") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    GenderOption(
                                        label = "Female",
                                        icon = Icons.Outlined.Female,
                                        selected = passenger.gender == "Female",
                                        accent = accentPurple,
                                        onClick = { onUpdatePassengerGender(index, "Female") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    GenderOption(
                                        label = "Other",
                                        icon = Icons.Outlined.Transgender,
                                        selected = passenger.gender == "Other",
                                        accent = accentPurple,
                                        onClick = { onUpdatePassengerGender(index, "Other") },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            // Age + Seat
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CustomTextField(
                                    value = passenger.age,
                                    onValueChange = { onUpdatePassengerAge(index, it) },
                                    label = "Age",
                                    placeholder = "Age",
                                    keyboardType = KeyboardType.Number,
                                    isError = passenger.ageError.isNotEmpty(),
                                    errorMessage = passenger.ageError,
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = passenger.seatNumber,
                                    onValueChange = {},
                                    label = { Text("Seat No.") },
                                    placeholder = { Text("Select Seat") },
                                    trailingIcon = { Icon(imageVector = Icons.Outlined.ExpandMore, contentDescription = null) },
                                    enabled = false,
                                    modifier = Modifier.weight(2f)
                                )
                            }
                        }
                    }
                }

                // Contact Details Section
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Contact Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        CustomTextField(
                            value = uiState.mobileNumber,
                            onValueChange = onUpdateMobile,
                            label = "Mobile Number",
                            placeholder = "Enter mobile number",
                            leadingIcon = Icons.Default.Phone,
                            keyboardType = KeyboardType.Phone,
                            isError = uiState.mobileError.isNotEmpty(),
                            errorMessage = uiState.mobileError
                        )
                        CustomTextField(
                            value = uiState.emailAddress,
                            onValueChange = onUpdateEmail,
                            label = "Email Address",
                            placeholder = "Enter email address",
                            leadingIcon = Icons.Default.Email,
                            keyboardType = KeyboardType.Email,
                            isError = uiState.emailError.isNotEmpty(),
                            errorMessage = uiState.emailError
                        )
                    }
                }

                // Trust banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(greenBannerBg)
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "👍 27 Lakh+ users secured their trip!",
                        color = greenBannerText,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }

                // Nxt Assured card
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {

                            Image(
                                painter = painterResource(id = R.drawable.nxt_assured_badge),
                                contentDescription = "Nxt Assured",
                                modifier = Modifier.height(28.dp),
                                contentScale = ContentScale.Fit
                            )

                            AssistChip(
                                onClick = {},
                                label = { Text("BADGE", fontWeight = FontWeight.SemiBold) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = Color(0xFFE0F2FE),
                                    labelColor = primaryBlue
                                )
                            )
                        }
                        Text(
                            text = "Secure your trip with Free Cancellation & instant refunds @ only ₹39",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        // Use ViewModel state instead of local
                        val refundSelected = uiState.includeRefund
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            InsuranceOptionRow(
                                title = "Yes I want full refund",
                                selected = refundSelected,
                                onClick = { onRefundSelected(true) }
                            ) {
                                Column(modifier = Modifier.padding(start = 8.dp)) {
                                    BenefitLine(text = "150% Refund on Bus cancellation")
                                    BenefitLine(text = "100% Refund on Bus Delays & Plan Changes")
                                }
                            }
                            InsuranceOptionRow(
                                title = "No, I don't want this",
                                selected = !refundSelected,
                                onClick = { onRefundSelected(false) }
                            ) {}
                        }
                    }
                }

                // ACKO card
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFEAF5FF), // very light blue top
                                        Color(0xFFD6ECFF)  // slightly deeper light blue bottom
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 36.dp), // leave room for the checkbox
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "ACKO General Travel Insurance",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Secure your Trip with Travel Insurance for just ₹10 per person",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF4B5563)
                            )
                            Text(
                                text = "Know More",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFDC2626)
                            )
                        }

                        Checkbox(
                            checked = uiState.includeAckoInsurance,
                            onCheckedChange = { onAckoSelected(it) },
                            modifier = Modifier.align(Alignment.BottomEnd)
                        )
                    }
                }

                // Error message
                if (uiState.errorMessage.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = uiState.errorMessage,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Footer button
            Surface(color = Color.White, shadowElevation = 2.dp) {
                Button(
                    onClick = onProceed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Proceed to Payment",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(name = "Passenger Details", showBackground = true, showSystemUi = true)
@Composable
private fun PassengerDetailsScreenPreview() {
    val nav = rememberNavController()
    val sampleBus = Bus(
        id = "BUS123",
        operatorName = "Blue Bus",
        busType = "A/C Sleeper",
        departureTime = "21:00",
        arrivalTime = "06:00",
        duration = "9h 0m",
        distance = "500km",
        price = 1200,
        availableSeats = 12,
        amenities = listOf("AC", "Blanket", "Charging"),
        rating = 4.4f,
        totalSeats = 24
    )
    var state by remember {
        mutableStateOf(
            PassengerDetailsUiState(
                bus = sampleBus,
                selectedSeats = listOf("1A", "1B"),
                totalAmount = 2499,
                fromCity = "City A",
                toCity = "City B",
                selectedDate = "2025/10/20",
                passengers = listOf(
                    PassengerInput(name = "", age = "", gender = "Male", seatNumber = "1A"),
                    PassengerInput(name = "", age = "", gender = "", seatNumber = "1B")
                ),
                mobileNumber = "",
                emailAddress = ""
            )
        )
    }

    PassengerDetailsContent(
        uiState = state,
        onUpdatePassengerName = { i, v ->
            state = state.copy(passengers = state.passengers.toMutableList().also { list ->
                if (i in list.indices) list[i] = list[i].copy(name = v)
            })
        },
        onUpdatePassengerAge = { i, v ->
            state = state.copy(passengers = state.passengers.toMutableList().also { list ->
                if (i in list.indices) list[i] = list[i].copy(age = v)
            })
        },
        onUpdatePassengerGender = { i, v ->
            state = state.copy(passengers = state.passengers.toMutableList().also { list ->
                if (i in list.indices) list[i] = list[i].copy(gender = v)
            })
        },
        onUpdateMobile = { v -> state = state.copy(mobileNumber = v) },
        onUpdateEmail = { v -> state = state.copy(emailAddress = v) },
        onProceed = {},
        onNavigateUp = {},
        navController = nav,
        snackbarHostState = remember { SnackbarHostState() },
        onRefundSelected = { selected -> state = state.copy(includeRefund = selected) },
        onAckoSelected = { selected -> state = state.copy(includeAckoInsurance = selected) }
    )
}

@Composable
private fun GenderOption(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) accent else MaterialTheme.colorScheme.outline
    val bg = if (selected) accent.copy(alpha = 0.08f) else Color.Transparent
    val labelColor = if (selected) accent else MaterialTheme.colorScheme.onSurface

    Surface(
        tonalElevation = if (selected) 2.dp else 0.dp,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = label, tint = labelColor)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = labelColor)
        }
    }
}

@Composable
private fun InsuranceOptionRow(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val border = if (selected) BorderStroke(2.dp, Color(0xFF1878D8)) else BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
    val bg = if (selected) Color(0xFFE8F3FE) else MaterialTheme.colorScheme.surface

    Surface(
        shape = RoundedCornerShape(12.dp),
        border = border,
        color = bg,
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // Custom radio visual
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (selected) Color(0xFF1878D8) else Color.Transparent)
                    .then(Modifier),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold)
                content()
            }
        }
    }
}

@Composable
private fun BenefitLine(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF22C55E),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall)
    }
}