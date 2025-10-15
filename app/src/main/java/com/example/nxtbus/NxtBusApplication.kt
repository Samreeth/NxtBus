package com.example.nxtbus

import android.app.Application
import com.razorpay.Checkout
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NxtBusApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Checkout.preload(applicationContext)
    }
}
