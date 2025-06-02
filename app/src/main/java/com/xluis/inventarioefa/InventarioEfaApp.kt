package com.xluis.inventarioefa

import android.app.Application
import com.google.firebase.FirebaseApp

class InventarioEfaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}