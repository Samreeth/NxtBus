package com.example.nxtbus.presentation.screens.c_seats

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nxtbus.data.a_model.Seat
import com.example.nxtbus.domain.repository.IBusRepository
import com.example.nxtbus.domain.usecase.GetSeatsUseCase
import com.example.nxtbus.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeatSelectionViewModel @Inject constructor(
    private val getSeatsUseCase: GetSeatsUseCase,
    private val busRepository: IBusRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeatSelectionUiState())
    val uiState: StateFlow<SeatSelectionUiState> = _uiState.asStateFlow()

    private val busId: String = savedStateHandle.get<String>("busId") ?: ""
    private val fromCity: String = savedStateHandle.get<String>("fromCity") ?: ""
    private val toCity: String = savedStateHandle.get<String>("toCity") ?: ""
    private val date: String = savedStateHandle.get<String>("date") ?: ""

    init {
        _uiState.value = _uiState.value.copy(
            fromCity = fromCity,
            toCity = toCity,
            selectedDate = date
        )
        loadBusAndSeats()
    }

    private fun loadBusAndSeats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")

            try {
                val bus = busRepository.getBusById(busId, fromCity, toCity, date)
                if (bus != null) {
                    _uiState.value = _uiState.value.copy(bus = bus)

                    // Set initial tab based on bus type
                    val initialTab = if (bus.busType.contains("Sleeper")) SeatTab.SLEEPER else SeatTab.SEATER
                    _uiState.value = _uiState.value.copy(selectedTab = initialTab)

                    // Load seats
                    getSeatsUseCase(bus)
                        .onSuccess { seats ->
                            _uiState.value = _uiState.value.copy(
                                seats = seats,
                                isLoading = false
                            )
                        }
                        .onFailure { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = exception.message ?: "Failed to load seats"
                            )
                        }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Bus not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "An error occurred"
                )
            }
        }
    }

    fun selectTab(tab: SeatTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun toggleSeatSelection(seat: Seat) {
        if (!seat.isAvailable) return

        val currentSelectedSeats = _uiState.value.selectedSeats
        val seatIndex = _uiState.value.seats.indexOfFirst { it.seatNumber == seat.seatNumber }

        if (seat.isSelected) {
            // Deselect seat
            val updatedSeats = _uiState.value.seats.toMutableList()
            updatedSeats[seatIndex] = seat.copy(isSelected = false)

            val updatedSelectedSeats = currentSelectedSeats.filter { it.seatNumber != seat.seatNumber }

            _uiState.value = _uiState.value.copy(
                seats = updatedSeats,
                selectedSeats = updatedSelectedSeats,
                totalAmount = updatedSelectedSeats.sumOf { it.price }
            )
        } else {
            // Select seat (check max limit)
            if (currentSelectedSeats.size >= Constants.MAX_SEAT_SELECTION) {
                // Show error or toast - for now just return
                return
            }

            val updatedSeats = _uiState.value.seats.toMutableList()
            updatedSeats[seatIndex] = seat.copy(isSelected = true)

            val updatedSelectedSeats = currentSelectedSeats + seat.copy(isSelected = true)

            _uiState.value = _uiState.value.copy(
                seats = updatedSeats,
                selectedSeats = updatedSelectedSeats,
                totalAmount = updatedSelectedSeats.sumOf { it.price }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = "")
    }
}
