package com.example.nxtbus.presentation.screens.b_search

import com.example.nxtbus.data.a_model.Bus


data class SearchResultsUiState(
    val buses: List<Bus> =emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val fromCity: String = "",
    val toCity: String = "",
    val selectedDate: String = "",
    val sortBy: SortOption = SortOption.DEPARTURE_TIME,
    val showSortDialog: Boolean = false,
    val showFilterDialog: Boolean = false
)

enum class SortOption(val displayName: String) {
    DEPARTURE_TIME("Departure Time"),
    PRICE_LOW_TO_HIGH("Price: Low to High"),
    PRICE_HIGH_TO_LOW("Price: High to Low"),
    DURATION("Duration"),
    SEATS_AVAILABLE("Seats Available")
}
