package com.example.shubhammishra.notes

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import com.hololo.tutorial.library.TutorialActivity
import android.graphics.Color.parseColor
import android.preference.PreferenceManager
import com.hololo.tutorial.library.Step


class OnboardActivity : TutorialActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this)
        addFragment(Step.Builder().setTitle("Notes App")
                .setContent("This app let you keep your personal data like:\nNotes,Snaps(Memorable Images),Todos List,Voice Recordings systematically.\nYour data is safe as it is linked to your account,so you can access it anytime,on any device by just logging in.")
                .setBackgroundColor(Color.parseColor("#2c3e50")) // int background color
                .setDrawable(R.drawable.ic_launcher_my_project)
                .setSummary("")
                .build())
        addFragment(Step.Builder().setTitle("FireBase Database Info")
                .setContent("Firebase is developed by Google,where your data is saved safely on online database.")
                .setBackgroundColor(Color.parseColor("#2c3e50")) // int background color
                .setDrawable(R.drawable.firebase)
                .setSummary("Your data is saved online which helps to save your Phone's Memory")
                .build())
        if(sharedPreferences.getBoolean("Finished",false))
        {
            startActivity(Intent(this@OnboardActivity,Welcome::class.java))
            finish()
        }
    }

    @SuppressLint("CommitPrefEdits")
    override fun finishTutorial() {

        super.finishTutorial()
        val sharedPreferencesEditor= PreferenceManager.getDefaultSharedPreferences(this@OnboardActivity).edit()
        sharedPreferencesEditor.putBoolean("Finished",true)
        sharedPreferencesEditor.apply()
        startActivity(Intent(this@OnboardActivity,Welcome::class.java))

    }
}
