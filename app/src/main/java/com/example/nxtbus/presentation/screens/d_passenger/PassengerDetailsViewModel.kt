package com.example.nxtbus.presentation.screens.d_passenger

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nxtbus.data.a_model.*
import com.example.nxtbus.domain.repository.IBusRepository
import com.example.nxtbus.domain.usecase.BookTicketUseCase
import com.example.nxtbus.domain.usecase.ValidatePassengerUseCase
//import com.example.nxtbus.utils.Constants
import com.example.nxtbus.utils.DateUtils
import com.example.nxtbus.utils.PriceUtils
import com.example.nxtbus.utils.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PassengerDetailsViewModel @Inject constructor(
    private val busRepository: IBusRepository,
    private val bookTicketUseCase: BookTicketUseCase,
    private val validatePassengerUseCase: ValidatePassengerUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PassengerDetailsUiState())
    val uiState: StateFlow<PassengerDetailsUiState> = _uiState.asStateFlow()

    private val busId: String = savedStateHandle.get<String>("busId") ?: ""
    private val selectedSeatsString: String = savedStateHandle.get<String>("selectedSeats") ?: ""
    private val baseTotalAmount: Int = savedStateHandle.get<String>("totalAmount")?.toIntOrNull() ?: 0
    private val fromCity: String = savedStateHandle.get<String>("fromCity") ?: ""
    private val toCity: String = savedStateHandle.get<String>("toCity") ?: ""
    private val date: String = savedStateHandle.get<String>("date") ?: ""

    private val refundAddon = 39
    private val ackoPerPerson = 10

    init {
        val selectedSeats = selectedSeatsString.split(",").map { it.trim() }.filter { it.isNotBlank() }
        val passengers = selectedSeats.map { seat ->
            PassengerInput(seatNumber = seat)
        }
        _uiState.value = _uiState.value.copy(
            selectedSeats = selectedSeats,
            totalAmount = baseTotalAmount,
            fromCity = fromCity,
            toCity = toCity,
            selectedDate = date,
            passengers = passengers
        )
        recalcTotal()
        loadBus()
    }

    private fun loadBus() {
        viewModelScope.launch {
            try {
                val bus = busRepository.getBusById(busId, fromCity, toCity, date)
                _uiState.value = _uiState.value.copy(bus = bus)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to load bus details"
                )
            }
        }
    }

    private fun recalcTotal() {
        val current = _uiState.value
        val extras = (if (current.includeRefund) refundAddon else 0) +
                (if (current.includeAckoInsurance) ackoPerPerson * current.selectedSeats.size else 0)
        _uiState.value = current.copy(totalAmount = baseTotalAmount + extras)
    }

    fun setRefundSelected(selected: Boolean) {
        _uiState.value = _uiState.value.copy(includeRefund = selected)
        recalcTotal()
    }

    fun setAckoSelected(selected: Boolean) {
        _uiState.value = _uiState.value.copy(includeAckoInsurance = selected)
        recalcTotal()
    }

    fun updatePassengerName(index: Int, name: String) {
        val list = _uiState.value.passengers.toMutableList()
        if (index in list.indices) {
            val p = list[index]
            list[index] = p.copy(name = name, nameError = "")
            _uiState.value = _uiState.value.copy(passengers = list)
        }
    }

    fun updatePassengerAge(index: Int, age: String) {
        val list = _uiState.value.passengers.toMutableList()
        if (index in list.indices) {
            val p = list[index]
            list[index] = p.copy(age = age, ageError = "")
            _uiState.value = _uiState.value.copy(passengers = list)
        }
    }

    fun updatePassengerGender(index: Int, gender: String) {
        val list = _uiState.value.passengers.toMutableList()
        if (index in list.indices) {
            val p = list[index]
            list[index] = p.copy(gender = gender)
            _uiState.value = _uiState.value.copy(passengers = list)
        }
    }

    // Build a valid dummy email from phone
    private fun buildDummyEmail(phone: String): String = "$phone@nxtdummy.com"
    private fun isAutoEmail(email: String): Boolean = email.endsWith("@nxtdummy.com")

    fun updateMobileNumber(mobile: String) {
        val current = _uiState.value
        val shouldAutoFillEmail = current.emailAddress.isBlank() || isAutoEmail(current.emailAddress)
        val newEmail = if (shouldAutoFillEmail && mobile.isNotBlank()) buildDummyEmail(mobile) else current.emailAddress
        _uiState.value = current.copy(
            mobileNumber = mobile,
            mobileError = "",
            emailAddress = newEmail,
            emailError = if (shouldAutoFillEmail) "" else current.emailError
        )
    }

    fun updateEmailAddress(email: String) {
        _uiState.value = _uiState.value.copy(
            emailAddress = email,
            emailError = ""
        )
    }

    fun proceedToPayment(onBookingSuccess: (String) -> Unit) {
        val current = _uiState.value

        // Validate passengers count
        if (current.passengers.size != current.selectedSeats.size) {
            _uiState.value = current.copy(errorMessage = "Please provide details for all selected seats")
            return
        }

        // Validate passengers
        var valid = true
        val updatedPassengers = current.passengers.map { p ->
            val nameRes = ValidationUtils.validateName(p.name)
            val ageRes = ValidationUtils.validateAge(p.age)
            // Gender validation: must be selected
            val genderOk = p.gender.isNotBlank()

            if (!nameRes.isValid || !ageRes.isValid || !genderOk) valid = false
            p.copy(
                nameError = if (!nameRes.isValid) nameRes.errorMessage else "",
                ageError = if (!ageRes.isValid) ageRes.errorMessage else ""
            )
        }

        // Validate contact details
        val mobileRes = ValidationUtils.validateMobile(current.mobileNumber)
        val emailRes = ValidationUtils.validateEmail(current.emailAddress)
        if (!mobileRes.isValid || !emailRes.isValid) valid = false

        if (!valid) {
            _uiState.value = current.copy(
                passengers = updatedPassengers,
                mobileError = if (!mobileRes.isValid) mobileRes.errorMessage else "",
                emailError = if (!emailRes.isValid) emailRes.errorMessage else "",
                errorMessage = if (updatedPassengers.any { it.nameError.isNotEmpty() || it.ageError.isNotEmpty() } || !genderAllSelected(updatedPassengers))
                    "Please correct passenger details"
                else "Please correct contact details"
            )
            return
        }

        // Create booking
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isBooking = true, errorMessage = "")
            try {
                val bus = current.bus ?: throw Exception("Bus details not available")

                // Ensure seat assignment aligns
                val passengers = updatedPassengers.mapIndexed { i, p ->
                    Passenger(
                        name = p.name,
                        age = p.age.toInt(),
                        gender = p.gender,
                        seatNumber = current.selectedSeats.getOrNull(i) ?: p.seatNumber
                    )
                }

                val contactDetails = ContactDetails(
                    mobileNumber = current.mobileNumber,
                    emailAddress = current.emailAddress
                )

                val booking = Booking(
                    pnr = PriceUtils.generatePNR(),
                    bus = bus,
                    selectedSeats = current.selectedSeats,
                    passengers = passengers,
                    contactDetails = contactDetails,
                    journeyDate = current.selectedDate,
                    fromCity = current.fromCity,
                    toCity = current.toCity,
                    totalAmount = current.totalAmount,
                    bookingDate = DateUtils.getCurrentBookingDate()
                )

                bookTicketUseCase(booking)
                    .onSuccess { pnr ->
                        _uiState.value = _uiState.value.copy(isBooking = false)
                        onBookingSuccess(pnr)
                    }
                    .onFailure { exception ->
                        _uiState.value = _uiState.value.copy(
                            isBooking = false,
                            errorMessage = exception.message ?: "Booking failed"
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isBooking = false,
                    errorMessage = e.message ?: "An error occurred"
                )
            }
        }
    }

    private fun genderAllSelected(list: List<PassengerInput>): Boolean = list.all { it.gender.isNotBlank() }

    fun validateForm(): Boolean {
        val current = _uiState.value
        var valid = true

        if (current.passengers.size != current.selectedSeats.size) {
            _uiState.value = current.copy(errorMessage = "Please provide details for all selected seats")
            return false
        }

        val updatedPassengers = current.passengers.map { p ->
            val nameRes = ValidationUtils.validateName(p.name)
            val ageRes = ValidationUtils.validateAge(p.age)
            val genderOk = p.gender.isNotBlank()
            if (!nameRes.isValid || !ageRes.isValid || !genderOk) valid = false
            p.copy(
                nameError = if (!nameRes.isValid) nameRes.errorMessage else "",
                ageError = if (!ageRes.isValid) ageRes.errorMessage else ""
            )
        }
        val mobileRes = ValidationUtils.validateMobile(current.mobileNumber)
        val emailRes = ValidationUtils.validateEmail(current.emailAddress)
        if (!mobileRes.isValid || !emailRes.isValid) valid = false

        _uiState.value = current.copy(
            passengers = updatedPassengers,
            mobileError = if (!mobileRes.isValid) mobileRes.errorMessage else "",
            emailError = if (!emailRes.isValid) emailRes.errorMessage else "",
            errorMessage = if (!valid) "Please fix the highlighted fields" else ""
        )

        return valid
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = "")
    }
}