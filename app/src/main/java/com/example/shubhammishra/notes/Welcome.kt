package com.example.shubhammishra.notes

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import kotlinx.android.synthetic.main.activity_welcome.*

class Welcome : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        val handler=Handler()
        handler.postDelayed(object:Runnable{
            override fun run() {
                welcome_Progress.visibility= View.GONE
                startActivity(Intent(this@Welcome,Authentication::class.java))
                finish()
            }
        },4000)
    }
}
