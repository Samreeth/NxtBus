package com.example.nxtbus.presentation.screens.a_home

data class HomeUiState(
    val fromCity: String = "",
    val toCity: String = "",
    val selectedDate: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val canSearch: Boolean = true
)
