package com.dotline

import android.app.Application
import android.support.multidex.MultiDex
import com.google.firebase.database.FirebaseDatabase

class MyApp :Application() {
    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        FirebaseDatabase.getInstance().setPersistenceCacheSizeBytes(100 * 1024 * 1024);
    }
}