package com.example.nxtbus.presentation.screens.e_confirmation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nxtbus.domain.repository.IBookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingConfirmationViewModel @Inject constructor(
    private val bookingRepository: IBookingRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingConfirmationUiState())
    val uiState: StateFlow<BookingConfirmationUiState> = _uiState.asStateFlow()

    private val pnr: String = savedStateHandle.get<String>("pnr") ?: ""

    init {
        loadBookingDetails()
    }

    private fun loadBookingDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val booking = bookingRepository.getBookingByPNR(pnr)
                _uiState.value = _uiState.value.copy(
                    booking = booking,
                    isLoading = false,
                    errorMessage = if (booking == null) "Booking not found" else ""
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load booking details"
                )
            }
        }
    }

    // Cancel the current booking; returns true on success so UI can navigate
    suspend fun cancelBooking(): Boolean {
        return try {
            bookingRepository.deleteBooking(pnr)
            true
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                errorMessage = e.message ?: "Failed to cancel booking"
            )
            false
        }
    }
}
