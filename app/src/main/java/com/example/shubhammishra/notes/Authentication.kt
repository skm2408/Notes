package com.example.shubhammishra.notes

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_authentication.*

class Authentication : AppCompatActivity() {
    lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth= FirebaseAuth.getInstance()
        if(auth.currentUser!=null)
        {
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        setContentView(R.layout.activity_authentication)
        supportFragmentManager.beginTransaction().replace(R.id.mainFrame,MainFragment()).commit()

    }
}
