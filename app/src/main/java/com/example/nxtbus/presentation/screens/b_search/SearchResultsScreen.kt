package com.example.nxtbus.presentation.screens.b_search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.SwapVert
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
import com.example.nxtbus.presentation.components.BottomNavigationBar
import com.example.nxtbus.presentation.components.BusCard
import com.example.nxtbus.presentation.components.ErrorMessage
import com.example.nxtbus.presentation.components.LoadingIndicator
import com.example.nxtbus.presentation.navigation.Screen
import com.example.nxtbus.presentation.theme.NxtBusTheme
import com.example.nxtbus.presentation.theme.PrimaryBlue

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SearchResultsScreenPreview() {
    NxtBusTheme {
        // Note: This won't work fully in preview due to ViewModel dependencies
        // But shows the UI structure
        Surface {
            Column(modifier = Modifier.fillMaxSize()) {
                Text("Search Results Preview - Run app to see full functionality")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(
    navController: NavController,
    viewModel: SearchResultsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Search Results",
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
            // Results header with sort and filter
            Surface(
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${uiState.buses.size} buses found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Sort pill
                        Button(
                            onClick = { viewModel.showSortDialog() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE9F2FF),
                                contentColor = PrimaryBlue
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SwapVert,
                                contentDescription = "Sort",
                                tint = PrimaryBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sort",
                                color = PrimaryBlue,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Filter pill
                        Button(
                            onClick = { /* Show filter sheet/dialog */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE9F2FF),
                                contentColor = PrimaryBlue
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FilterList,
                                contentDescription = "Filter",
                                tint = PrimaryBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Filter",
                                color = PrimaryBlue,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // Content
            when {
                uiState.isLoading -> {
                    LoadingIndicator(
                        message = "Searching buses...",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                uiState.errorMessage.isNotEmpty() -> {
                    ErrorMessage(
                        message = uiState.errorMessage,
                        onRetry = { viewModel.searchBuses() },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                uiState.buses.isEmpty() -> {
                    ErrorMessage(
                        message = "No buses found for this route on ${uiState.selectedDate}",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(uiState.buses) { bus ->
                            BusCard(
                                bus = bus,
                                fromCity = uiState.fromCity,
                                toCity = uiState.toCity,
                                onClick = {
                                    navController.navigate(
                                        Screen.SeatSelection.createRoute(
                                            bus.id,
                                            uiState.fromCity,
                                            uiState.toCity,
                                            uiState.selectedDate
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Sort Dialog
    if (uiState.showSortDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideSortDialog() },
            title = { Text("Sort by") },
            text = {
                Column {
                    SortOption.entries.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = uiState.sortBy == option,
                                onClick = { viewModel.sortBuses(option) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(option.displayName)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.hideSortDialog() }) {
                    Text("Done")
                }
            }
        )
    }
}