package com.example.shubhammishra.notes.Fragments

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shubhammishra.notes.Adapters.AudioAdapter
import com.example.shubhammishra.notes.Classes.Recording
import com.example.shubhammishra.notes.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.list_view_voice.view.*

class VoiceFragment : android.support.v4.app.Fragment() {
    lateinit var view1: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view1 = inflater!!.inflate(R.layout.list_view_voice, null)
        return view1
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        var listAudio=ArrayList<Recording>()
        var audioAdapter=AudioAdapter(listAudio)
        val uid= FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference=FirebaseDatabase.getInstance().reference.child(uid).child("Recordings")
        view1.mainViewVoice.layoutManager=LinearLayoutManager(view1.context)
        view1.mainViewVoice.adapter=audioAdapter
        databaseReference.addChildEventListener(object:ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val audio=p0.getValue(Recording::class.java)
                listAudio.add(audio!!)
                audioAdapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
        super.onActivityCreated(savedInstanceState)

    }
}