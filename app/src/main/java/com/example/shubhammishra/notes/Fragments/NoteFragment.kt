package com.example.shubhammishra.notes.Fragments

import android.app.Fragment
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shubhammishra.notes.Adapters.NotesAdapter
import com.example.shubhammishra.notes.Classes.Notes
import com.example.shubhammishra.notes.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.list_view_notes.view.*

class NoteFragment : android.support.v4.app.Fragment() {
    lateinit var view1: View
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        var listNotes = ArrayList<Notes>()
        val notesAdapter = NotesAdapter(listNotes)
        view1.mainViewNotes.layoutManager = LinearLayoutManager(view1.context)
        view1.mainViewNotes.adapter = notesAdapter
        val auth = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference = FirebaseDatabase.getInstance().reference.child(auth).child("Notes")
        databaseReference.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val note=p0.getValue(Notes::class.java)
                if(note==null)
                {
                    view1.mainViewNotes.visibility = View.GONE
                    view1.empty_view_notes.visibility = View.VISIBLE
                    view1.tvMessage_notes.text = "Nothing to Display Yet\nAdded Notes will be displayed here"
                }
                else {
                    view1.mainViewNotes.visibility = View.VISIBLE
                    view1.empty_view_notes.visibility = View.GONE
                }
            }
        })

        notesAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                if (listNotes.isEmpty()) {
                    view1.mainViewNotes.visibility = View.GONE
                    view1.empty_view_notes.visibility = View.VISIBLE
                    view1.tvMessage_notes.text = "Nothing to Display Yet\nAdded Notes will be displayed here"
                } else {
                    view1.mainViewNotes.visibility = View.VISIBLE
                    view1.empty_view_notes.visibility = View.GONE
                }
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {

            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {

            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {

            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {

            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {

            }
        })
        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val notes = p0.getValue(Notes::class.java)
                listNotes.add(notes!!)
                notesAdapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view1 = inflater!!.inflate(R.layout.list_view_notes, null)
        return view1
    }
}