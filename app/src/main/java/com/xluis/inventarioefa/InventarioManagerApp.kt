package com.xluis.inventarioefa

import android.app.Application
import com.google.firebase.FirebaseApp
import com.xluis.inventarioefa.data.Database.Datastore.UserDataStore
import com.xluis.inventarioefa.di.AppDependencies

class InventarioManagerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        AppDependencies.init(this)
        AppDependencies.setupFactories()
        UserDataStore.init(this)
    }
}
