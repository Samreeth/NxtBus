package com.example.nxtbus.utils

object ValidationUtils {

    fun validateName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult(false, "Name is required")
            name.length < 2 -> ValidationResult(false, "Name must be at least 2 characters")
            !name.matches(Regex("^[a-zA-Z\\s]+$")) -> ValidationResult(false, "Name can only contain letters")
            else -> ValidationResult(true, "")
        }
    }

    fun validateAge(ageStr: String): ValidationResult {
        if (ageStr.isBlank()) {
            return ValidationResult(false, "Age is required")
        }

        val age = ageStr.toIntOrNull()
        return when {
            age == null -> ValidationResult(false, "Please enter a valid age")
            age < Constants.MIN_AGE -> ValidationResult(false, "Age must be at least ${Constants.MIN_AGE}")
            age > Constants.MAX_AGE -> ValidationResult(false, "Age cannot exceed ${Constants.MAX_AGE}")
            else -> ValidationResult(true, "")
        }
    }

    fun validateMobile(mobile: String): ValidationResult {
        return when {
            mobile.isBlank() -> ValidationResult(false, "Mobile number is required")
            !mobile.matches(Regex("^[0-9]{10}$")) -> ValidationResult(false, "Please enter a valid 10-digit mobile number")
            else -> ValidationResult(true, "")
        }
    }

    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult(false, "Email is required")
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> ValidationResult(false, "Please enter a valid email address")
            else -> ValidationResult(true, "")
        }
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String
)