package com.example.shubhammishra.notes.Fragments


import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.shubhammishra.notes.Classes.Notes
import com.example.shubhammishra.notes.MainActivity
import com.example.shubhammishra.notes.Manifest

import com.example.shubhammishra.notes.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_notes.view.*
import java.io.*

class Notes : Fragment() {
    lateinit var view1: View
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val auth = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference = FirebaseDatabase.getInstance().reference.child(auth).child("Notes").push()
        view1.saveNote.setOnClickListener {
            val title = view1.etTitle.text.toString()
            val text = view1.etNotes.text.toString()
            if (title.isEmpty() || text.isEmpty()) {
                Toast.makeText(view1.context, "Empty Fields!!", Toast.LENGTH_SHORT).show()
            } else {
                val notes = Notes(System.currentTimeMillis().toString(),title,text)
                databaseReference.setValue(notes)
                Snackbar.make(view1, "Saved Successfully", Snackbar.LENGTH_LONG).setAction("Go to Homepage",object:View.OnClickListener{
                    override fun onClick(v: View?) {
                        activity!!.finish()
                    }
                }).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        view1 = inflater.inflate(R.layout.fragment_notes, container, false)
        return view1
    }
}
