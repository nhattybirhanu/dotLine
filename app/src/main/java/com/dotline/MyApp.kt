package com.dotline

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.support.multidex.MultiDex
import com.google.firebase.database.FirebaseDatabase


class MyApp :Application() {
    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this);
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        FirebaseDatabase.getInstance().setPersistenceCacheSizeBytes(100 * 1024 * 1024);
    }
}