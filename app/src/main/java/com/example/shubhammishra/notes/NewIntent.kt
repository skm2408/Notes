package com.example.shubhammishra.notes

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.shubhammishra.notes.Fragments.Notes
import com.example.shubhammishra.notes.Fragments.Todos
import com.example.shubhammishra.notes.Fragments.record
import kotlinx.android.synthetic.main.layout_toolbar.*

class NewIntent : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_intent)
        val pressed = intent.getStringExtra("Pressed")
        if (pressed.equals("Todos")) {
            toolBarImage.setImageResource(R.drawable.back_home_todos)
            toolBarText.text = "TODOS"
            supportFragmentManager.beginTransaction().replace(R.id.mainFrameLayout, Todos()).commit()
        }
        else if(pressed.equals("Recording"))
        {
            toolBarImage.setImageResource(R.drawable.back_home_camera)
            toolBarText.text="VOICE"
            supportFragmentManager.beginTransaction().replace(R.id.mainFrameLayout, record()).commit()
        }
        else if(pressed.equals("Notes"))
        {
            toolBarImage.setImageResource(R.drawable.back_home_notes)
            toolBarText.text="Note"
            supportFragmentManager.beginTransaction().replace(R.id.mainFrameLayout, Notes()).commit()
        }
    }
}
