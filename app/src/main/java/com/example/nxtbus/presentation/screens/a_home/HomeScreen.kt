package com.example.nxtbus.presentation.screens.a_home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.nxtbus.presentation.components.BottomNavigationBar
//import com.example.nxtbus.presentation.components.CustomDropdown
import com.example.nxtbus.presentation.navigation.Screen
import com.example.nxtbus.presentation.theme.NxtBusTheme
import com.example.nxtbus.presentation.theme.PrimaryBlue
import com.example.nxtbus.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    NxtBusTheme {
        HomeScreen(navController = rememberNavController())
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "NxtBus",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* Menu action */ }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Screen.Profile.route)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Search Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // From City Dropdown
                    CityDropdown(
                        selectedValue = uiState.fromCity,
                        onValueSelected = viewModel::updateFromCity,
                        label = "From",
                        placeholder = "Select departure city",
                        leadingIcon = Icons.Default.MyLocation
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Swap Button (circular, elevated like the mock)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                tonalElevation = 0.dp,
                                shadowElevation = 6.dp,
                                border = BorderStroke(1.dp, SolidColor(Color(0xFFE6EAF2))),
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable { viewModel.swapCities() }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.SwapVert,
                                        contentDescription = "Swap cities",
                                        tint = Color(0xFF6B5BFF),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(3.dp)
                                    .background(Color(0xFF6B5BFF), CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // To City Dropdown
                    CityDropdown(
                        selectedValue = uiState.toCity,
                        onValueSelected = viewModel::updateToCity,
                        label = "To",
                        placeholder = "Select destination city",
                        leadingIcon = Icons.Default.LocationOn
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Date Picker
                    DatePickerField(
                        selectedDate = uiState.selectedDate,
                        onDateSelected = viewModel::updateSelectedDate,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Upcoming four days row
                    UpcomingDaysRow(
                        selectedDate = uiState.selectedDate,
                        onDateSelected = viewModel::updateSelectedDate
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Search Button
                    Button(
                        onClick = {
                            if (uiState.canSearch) {
                                navController.navigate(
                                    Screen.SearchResults.createRoute(
                                        uiState.fromCity,
                                        uiState.toCity,
                                        uiState.selectedDate
                                    )
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = uiState.canSearch,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            disabledContainerColor = PrimaryBlue.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 10.dp,
                            focusedElevation = 8.dp,
                            hoveredElevation = 9.dp,
                            disabledElevation = 0.dp
                        )
                    ) {
                        Text(
                            text = "Search Buses",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Coupons carousel below the form
            Spacer(modifier = Modifier.height(20.dp))
            CouponsSection(
                onViewAll = { /* TODO: navigate to offers screen */ }
            )

            // Error Message
            if (uiState.errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = uiState.errorMessage,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CityDropdown(
    selectedValue: String,
    onValueSelected: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        // Small label with a thin divider to the right (like the mock)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF6B7A90)
            )
            Spacer(modifier = Modifier.width(6.dp))
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = Color(0xFFE6EAF2)
            )
        }

        // Dropdown anchor: pill container with icon, text, chevron
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
                    .clickable { expanded = true },
                shape = RoundedCornerShape(26.dp),
                color = Color(0xFFF4F7FB),
                border = BorderStroke(1.dp, SolidColor(Color(0xFFE1E7EF))),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Leading circular icon chip
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE9F2FF),
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp
                    ) {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Selected text or placeholder
                    Text(
                        text = if (selectedValue.isBlank()) placeholder else selectedValue,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selectedValue.isBlank()) Color(0xFF95A2B3) else Color(0xFF1B2B41),
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = Color(0xFF6B7A90)
                    )
                }
            }

            // Menu
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                Constants.CITIES.forEach { city ->
                    DropdownMenuItem(
                        text = { Text(city) },
                        onClick = {
                            onValueSelected(city)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerField(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    Column(modifier = modifier) {
        // Label + divider to match the pill inputs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Departure Date",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF6B7A90)
            )
            Spacer(modifier = Modifier.width(6.dp))
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = Color(0xFFE6EAF2)
            )
        }

        // Pill container acting as the date picker trigger
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            shape = RoundedCornerShape(26.dp),
            color = Color(0xFFF4F7FB),
            border = BorderStroke(1.dp, SolidColor(Color(0xFFE1E7EF))),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Leading circular icon chip
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFE9F2FF),
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = if (selectedDate.isBlank()) "Select Date" else selectedDate,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedDate.isBlank()) Color(0xFF95A2B3) else Color(0xFF1B2B41),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Color(0xFF6B7A90)
                )
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Date(millis)
                            val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
                            onDateSelected(formattedDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun UpcomingDaysRow(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    val baseDate: Date = remember(selectedDate) {
        try {
            if (selectedDate.isNotBlank()) sdf.parse(selectedDate) ?: Date() else Date()
        } catch (_: Exception) { Date() }
    }

    val upcoming = remember(baseDate) {
        (1..5).map { offset ->
            Calendar.getInstance().apply {
                time = baseDate
                add(Calendar.DAY_OF_YEAR, offset)
            }.time
        }
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(upcoming) { date ->
            val isSelected = sdf.format(date) == selectedDate
            DayChip(
                date = date,
                selected = isSelected,
                onClick = { onDateSelected(sdf.format(date)) }
            )
        }
    }
}

@Composable
private fun DayChip(
    date: Date,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val day = remember(date) { SimpleDateFormat("EEE", Locale.getDefault()).format(date) }
    val dayNum = remember(date) { SimpleDateFormat("d", Locale.getDefault()).format(date) }
    val month = remember(date) { SimpleDateFormat("MMM", Locale.getDefault()).format(date) }

    val bg = if (selected) Color.White else Color(0xFFF4F7FB)
    val border = if (selected) Color(0xFF6B5BFF) else Color(0xFFE1E7EF)
    val shadow = if (selected) 8.dp else 0.dp
    val dayColor = Color(0xFF6B7A90)
    val numColor = Color(0xFF1B2B41)
    val monColor = Color(0xFF9AA7B8)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = bg,
        border = BorderStroke(1.dp, border),
        shadowElevation = shadow,
        tonalElevation = 0.dp,
        modifier = modifier
            .widthIn(min = 56.dp)
            .heightIn(min = 64.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = day, style = MaterialTheme.typography.labelSmall, color = dayColor)
            Spacer(Modifier.height(1.dp))
            Text(
                text = dayNum,
                style = MaterialTheme.typography.titleSmall,
                color = numColor,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(1.dp))
            Text(text = month, style = MaterialTheme.typography.labelSmall, color = monColor)
        }
    }
}

@Composable
private fun CouponsSection(
    modifier: Modifier = Modifier,
    onViewAll: () -> Unit = {}
) {
    val coupons = remember {
        listOf(
            Coupon(
                title = "Save up to Rs. 200",
                subtitle = "on bus tickets",
                code = "NXTFIRST",
                bgColor = Color(0xFFE53935) // red
            ),
            Coupon(
                title = "Flat 10% OFF",
                subtitle = "for new users",
                code = "FLAT10",
                bgColor = Color(0xFF1976D2) // blue
            ),
            Coupon(
                title = "Extra Rs. 99 OFF",
                subtitle = "on UPI payments",
                code = "UPI100",
                bgColor = Color(0xFF00897B) // teal
            )
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bus Booking Discount Offers",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B2B41)
            )
            TextButton(onClick = onViewAll) {
                Text(text = "View All →", color = PrimaryBlue)
            }
        }

        val listState = rememberLazyListState()
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(coupons) { coupon ->
                CouponCard(coupon = coupon)
            }
        }

        // Dots indicator
        val currentIndex by remember { derivedStateOf { listState.firstVisibleItemIndex.coerceIn(0, coupons.lastIndex) } }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            coupons.forEachIndexed { index, _ ->
                val selected = index == currentIndex
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(width = if (selected) 18.dp else 6.dp, height = 6.dp)
                        .background(
                            color = if (selected) PrimaryBlue.copy(alpha = 0.9f) else Color(0xFFD7DFEA),
                            shape = RoundedCornerShape(3.dp)
                        )
                )
            }
        }
    }
}

private data class Coupon(
    val title: String,
    val subtitle: String,
    val code: String,
    val bgColor: Color
)

@Composable
private fun CouponCard(coupon: Coupon, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .width(260.dp)
            .height(140.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        val gradient = when (coupon.code.uppercase(Locale.getDefault())) {
            "NXTFIRST" -> Brush.linearGradient(
                colors = listOf(Color(0xFFFF5F6D), Color(0xFFFFC371))
            )
            "FLAT10" -> Brush.linearGradient(
                colors = listOf(Color(0xFF36D1DC), Color(0xFF5B86E5))
            )
            "UPI100" -> Brush.linearGradient(
                colors = listOf(Color(0xFF11998E), Color(0xFF38EF7D))
            )
            else -> Brush.linearGradient(
                colors = listOf(PrimaryBlue, PrimaryBlue.copy(alpha = 0.8f))
            )
        }

        Box(
            Modifier
                .fillMaxSize()
                .background(brush = gradient, shape = RoundedCornerShape(18.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val icon = when (coupon.code.uppercase(Locale.getDefault())) {
                    "NXTFIRST" -> Icons.Default.CardGiftcard
                    "FLAT10" -> Icons.Default.LocalOffer
                    "UPI100" -> Icons.Default.Payments
                    else -> Icons.Default.LocalOffer
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.18f),
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(28.dp)
                                .padding(4.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = coupon.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(6.dp))
                Text(
                    text = coupon.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Code pill
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.18f),
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp
                    ) {
                        val codeText = buildAnnotatedString {
                            append("Code: ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(coupon.code)
                            }
                        }
                        Text(
                            text = codeText,
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                    Text(
                        text = "T&C* Apply",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}
