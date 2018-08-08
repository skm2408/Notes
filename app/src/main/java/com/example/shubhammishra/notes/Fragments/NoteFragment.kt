package com.example.shubhammishra.notes.Fragments

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shubhammishra.notes.Adapters.NotesAdapter
import com.example.shubhammishra.notes.Classes.Notes
import com.example.shubhammishra.notes.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.list_view_notes.view.*

class NoteFragment: android.support.v4.app.Fragment() {
    lateinit var view1:View

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        var listNotes=ArrayList<Notes>()
        val notesAdapter=NotesAdapter(listNotes)
        view1.mainViewNotes.layoutManager=LinearLayoutManager(view1.context)
        view1.mainViewNotes.adapter=notesAdapter
        val auth= FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference=FirebaseDatabase.getInstance().reference.child(auth).child("Notes")
        databaseReference.addChildEventListener(object:ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val notes=p0.getValue(Notes::class.java)
                listNotes.add(notes!!)
                notesAdapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view1= inflater!!.inflate(R.layout.list_view_notes,null)
        return view1
    }
}