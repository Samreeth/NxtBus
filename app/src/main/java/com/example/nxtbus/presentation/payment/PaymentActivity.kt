package com.example.nxtbus.presentation.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.nxtbus.BuildConfig
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONArray
import org.json.JSONObject

class PaymentActivity : ComponentActivity(), PaymentResultListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Launch Razorpay checkout immediately
        startRazorpay()
    }

    private fun startRazorpay() {
        val amountPaise = intent.getLongExtra(EXTRA_AMOUNT_PAISE, 0L)
        val currency = intent.getStringExtra(EXTRA_CURRENCY) ?: "INR"
        val name = intent.getStringExtra(EXTRA_NAME) ?: "NxtBus"
        val description = intent.getStringExtra(EXTRA_DESCRIPTION) ?: "Bus ticket booking"
        val contact = intent.getStringExtra(EXTRA_CONTACT)
        val email = intent.getStringExtra(EXTRA_EMAIL)
        val upiOnly = intent.getBooleanExtra(EXTRA_UPI_ONLY, false)

        val checkout = Checkout()
        // Use BuildConfig value set in build.gradle (debug) for test mode
        checkout.setKeyID(BuildConfig.RAZORPAY_KEY_ID)

        val options = JSONObject().apply {
            put("name", name)
            put("description", description)
            put("currency", currency)
            put("amount", amountPaise) // amount in currency subunits, e.g., paise
            put("retry", JSONObject().apply {
                put("enabled", true)
                put("max_count", 1)
            })
            put("prefill", JSONObject().apply {
                if (!email.isNullOrBlank()) put("email", email)
                if (!contact.isNullOrBlank()) put("contact", contact)
            })
            // Theme customization (optional)
            put("theme", JSONObject().apply { put("color", "#1E88E5") })
        }

        if (upiOnly) {
            // Preselect UPI and hide other methods
            options.put("method", "upi")
            val methodsToHide = org.json.JSONArray().apply {
                put("card")
                put("netbanking")
                put("wallet")
                put("emi")
                put("paylater")
            }
            val hide = JSONObject().apply { put("method", methodsToHide) }
            val display = JSONObject().apply { put("hide", hide) }
            val config = JSONObject().apply { put("display", display) }
            options.put("config", config)
        }

        checkout.open(this, options)
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(RESULT_PAYMENT_ID, razorpayPaymentID ?: "")
        })
        finish()
    }

    override fun onPaymentError(code: Int, response: String?) {
        setResult(Activity.RESULT_CANCELED, Intent().apply {
            putExtra(RESULT_ERROR, response ?: "Payment failed")
            putExtra(RESULT_ERROR_CODE, code)
        })
        finish()
    }

    companion object {
        const val EXTRA_AMOUNT_PAISE = "extra_amount_paise"
        const val EXTRA_CURRENCY = "extra_currency"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_DESCRIPTION = "extra_description"
        const val EXTRA_CONTACT = "extra_contact"
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_UPI_ONLY = "extra_upi_only"

        const val RESULT_PAYMENT_ID = "result_payment_id"
        const val RESULT_ERROR = "result_error"
        const val RESULT_ERROR_CODE = "result_error_code"
    }
}
