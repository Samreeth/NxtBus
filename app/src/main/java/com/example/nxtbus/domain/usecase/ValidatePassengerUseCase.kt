package com.example.nxtbus.domain.usecase

import com.example.nxtbus.utils.ValidationUtils
import javax.inject.Inject

class ValidatePassengerUseCase @Inject constructor() {

    data class PassengerValidationData(
        val name: String,
        val age: String,
        val gender: String,
        val mobileNumber: String,
        val emailAddress: String
    )

    data class ValidationErrors(
        val nameError: String = "",
        val ageError: String = "",
        val mobileError: String = "",
        val emailError: String = ""
    )

    operator fun invoke(data: PassengerValidationData): Pair<Boolean, ValidationErrors> {
        val nameValidation = ValidationUtils.validateName(data.name)
        val ageValidation = ValidationUtils.validateAge(data.age)
        val mobileValidation = ValidationUtils.validateMobile(data.mobileNumber)
        val emailValidation = ValidationUtils.validateEmail(data.emailAddress)

        val errors = ValidationErrors(
            nameError = if (!nameValidation.isValid) nameValidation.errorMessage else "",
            ageError = if (!ageValidation.isValid) ageValidation.errorMessage else "",
            mobileError = if (!mobileValidation.isValid) mobileValidation.errorMessage else "",
            emailError = if (!emailValidation.isValid) emailValidation.errorMessage else ""
        )

        val isValid = nameValidation.isValid &&
                ageValidation.isValid &&
                mobileValidation.isValid &&
                emailValidation.isValid

        return Pair(isValid, errors)
    }
}
