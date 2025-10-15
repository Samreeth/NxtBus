package com.example.nxtbus.presentation.screens.b_search


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nxtbus.domain.usecase.SearchBusesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultsViewModel @Inject constructor(
    private val searchBusesUseCase: SearchBusesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchResultsUiState())
    val uiState: StateFlow<SearchResultsUiState> = _uiState.asStateFlow()

    private val fromCity: String = savedStateHandle.get<String>("fromCity") ?: ""
    private val toCity: String = savedStateHandle.get<String>("toCity") ?: ""
    private val date: String = savedStateHandle.get<String>("date") ?.replace("-", "/") ?: ""

    init {
        _uiState.value = _uiState.value.copy(
            fromCity = fromCity,
            toCity = toCity,
            selectedDate = date
        )
        searchBuses()
    }

    fun searchBuses() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")

            searchBusesUseCase(fromCity, toCity, date)
                .onSuccess { buses ->
                    _uiState.value = _uiState.value.copy(
                        buses = sortBuses(buses, _uiState.value.sortBy),
                        isLoading = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Failed to load buses"
                    )
                }
        }
    }

    fun showSortDialog() {
        _uiState.value = _uiState.value.copy(showSortDialog = true)
    }

    fun hideSortDialog() {
        _uiState.value = _uiState.value.copy(showSortDialog = false)
    }

    fun sortBuses(sortOption: SortOption) {
        val sortedBuses = sortBuses(_uiState.value.buses, sortOption)
        _uiState.value = _uiState.value.copy(
            buses = sortedBuses,
            sortBy = sortOption,
            showSortDialog = false
        )
    }

    private fun sortBuses(buses: List<com.example.nxtbus.data.a_model.Bus>, sortOption: SortOption) = when (sortOption) {
        SortOption.DEPARTURE_TIME -> buses.sortedBy { it.departureTime }
        SortOption.PRICE_LOW_TO_HIGH -> buses.sortedBy { it.price }
        SortOption.PRICE_HIGH_TO_LOW -> buses.sortedByDescending { it.price }
        SortOption.DURATION -> buses.sortedBy { it.duration.replace("[^0-9]".toRegex(), "").toIntOrNull() ?: 0 }
        SortOption.SEATS_AVAILABLE -> buses.sortedByDescending { it.availableSeats }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = "")
    }
}
