package com.example.shubhammishra.notes


import android.app.Application
import com.google.firebase.database.FirebaseDatabase


class Notes: Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

    }
}