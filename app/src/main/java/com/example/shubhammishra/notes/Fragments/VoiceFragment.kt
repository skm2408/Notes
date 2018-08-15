package com.example.shubhammishra.notes.Fragments

import android.app.Fragment
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shubhammishra.notes.Adapters.AudioAdapter
import com.example.shubhammishra.notes.Classes.Recording
import com.example.shubhammishra.notes.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.list_view_voice.view.*

class VoiceFragment : android.support.v4.app.Fragment() {
    lateinit var view1: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view1 = inflater!!.inflate(R.layout.list_view_voice, null)
        return view1
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        var listAudio = ArrayList<Recording>()
        var audioAdapter = AudioAdapter(listAudio)
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference = FirebaseDatabase.getInstance().reference.child(uid).child("Recordings")
        databaseReference.keepSynced(true)
        view1.mainViewVoice.layoutManager = GridLayoutManager(view1.context, 2)
        view1.mainViewVoice.adapter = audioAdapter
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val voice = p0.getValue(Recording::class.java)
                if (voice == null) {
                    view1.mainViewVoice.visibility = View.GONE
                    view1.empty_view_voice.visibility = View.VISIBLE
                    view1.tvMessage_voice.text = "Nothing to Display Yet\nAdded Recordings will be displayed here"
                } else {
                    view1.mainViewVoice.visibility = View.VISIBLE
                    view1.empty_view_voice.visibility = View.GONE
                }
            }
        })
        audioAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                if (listAudio.isEmpty()) {
                    view1.mainViewVoice.visibility = View.GONE
                    view1.empty_view_voice.visibility = View.VISIBLE
                    view1.tvMessage_voice.text = "Nothing to Display Yet\nAdded Recordings will be displayed here"
                } else {
                    view1.mainViewVoice.visibility = View.VISIBLE
                    view1.empty_view_voice.visibility = View.GONE
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
                val audio = p0.getValue(Recording::class.java)
                listAudio.add(audio!!)
                audioAdapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
        super.onActivityCreated(savedInstanceState)

    }
}