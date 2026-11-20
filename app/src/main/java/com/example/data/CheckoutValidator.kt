package com.example.data

object CheckoutValidator {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val TEN_DIGITS_REGEX = Regex("^\\d{10}$")
    private val SIX_DIGITS_REGEX = Regex("^\\d{6}$")

    fun validate(form: CheckoutFormState): CheckoutValidationErrors {
        val nameErr = if (form.fullName.trim().isEmpty()) {
            "Enter a fictional full name."
        } else null

        val trimmedEmail = form.email.trim()
        val emailErr = when {
            trimmedEmail.isEmpty() -> "Enter an email address (fictional is fine)."
            !EMAIL_REGEX.matches(trimmedEmail) -> "Enter a valid email format, such as student@example.com."
            else -> null
        }

        val trimmedMobile = form.mobileNumber.trim()
        val mobileErr = when {
            trimmedMobile.isEmpty() -> "Enter a 10-digit fictional mobile number."
            !TEN_DIGITS_REGEX.matches(trimmedMobile) -> "Mobile number must contain exactly 10 digits."
            else -> null
        }

        val addressErr = if (form.addressLine.trim().isEmpty()) {
            "Enter a fictional street or hostel address line."
        } else null

        val cityErr = if (form.city.trim().isEmpty()) {
            "Enter a city name."
        } else null

        val stateErr = when {
            form.state.trim().isEmpty() -> "Select a state from the prototype focus list."
            form.state !in NivoraCatalog.ALLOWED_CHECKOUT_STATES ->
                "Please choose Uttarakhand, Delhi, Haryana, or Uttar Pradesh."
            else -> null
        }

        val trimmedPin = form.pinCode.trim()
        val pinErr = when {
            trimmedPin.isEmpty() -> "Enter a 6-digit PIN code."
            !SIX_DIGITS_REGEX.matches(trimmedPin) -> "PIN code must contain exactly 6 digits."
            else -> null
        }

        return CheckoutValidationErrors(
            fullName = nameErr,
            email = emailErr,
            mobileNumber = mobileErr,
            addressLine = addressErr,
            city = cityErr,
            state = stateErr,
            pinCode = pinErr,
        )
    }
}
