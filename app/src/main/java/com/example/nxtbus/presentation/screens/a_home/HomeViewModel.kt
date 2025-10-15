package com.example.nxtbus.presentation.screens.a_home

import androidx.lifecycle.ViewModel
import com.example.nxtbus.utils.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // Set current date as default
        updateSelectedDate(DateUtils.getCurrentDate())
    }

    fun updateFromCity(city: String) {
        _uiState.value = _uiState.value.copy(
            fromCity = city,
            canSearch = validateSearchInputs(city, _uiState.value.toCity)
        )
    }

    fun updateToCity(city: String) {
        _uiState.value = _uiState.value.copy(
            toCity = city,
            canSearch = validateSearchInputs(_uiState.value.fromCity, city)
        )
    }

    fun updateSelectedDate(date: String) {
        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            canSearch = validateSearchInputs(_uiState.value.fromCity, _uiState.value.toCity)
        )
    }

    fun swapCities() {
        val currentFrom = _uiState.value.fromCity
        val currentTo = _uiState.value.toCity

        _uiState.value = _uiState.value.copy(
            fromCity = currentTo,
            toCity = currentFrom,
            canSearch = validateSearchInputs(currentTo, currentFrom)
        )
    }

    private fun validateSearchInputs(fromCity: String, toCity: String): Boolean {
        return fromCity.isNotBlank() &&
                toCity.isNotBlank() &&
                fromCity != toCity &&
                _uiState.value.selectedDate.isNotBlank()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = "")
    }
}
