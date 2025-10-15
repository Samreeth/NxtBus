package com.example.nxtbus.presentation.screens.f_tickets


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nxtbus.domain.repository.IBookingRepository
//import com.example.nxtbus.presentation.screens.f_tickets.MyTicketsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyTicketsViewModel @Inject constructor(
    private val bookingRepository: IBookingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyTicketsUiState())
    val uiState: StateFlow<MyTicketsUiState> = _uiState.asStateFlow()

    init {
        loadBookings()
    }

    fun loadBookings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val bookings = bookingRepository.getBookings()
                _uiState.value = _uiState.value.copy(
                    bookings = bookings.sortedByDescending { it.bookingDate },
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load bookings"
                )
            }
        }
    }
}