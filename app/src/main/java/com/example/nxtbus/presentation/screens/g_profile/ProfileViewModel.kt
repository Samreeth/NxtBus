package com.example.nxtbus.presentation.screens.g_profile

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
class ProfileViewModel @Inject constructor(
    private val bookingRepository: IBookingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            try {
                val bookings = bookingRepository.getBookings()
                _uiState.value = _uiState.value.copy(
                    totalBookings = bookings.size
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
