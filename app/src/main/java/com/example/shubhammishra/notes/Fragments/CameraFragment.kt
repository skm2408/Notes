package com.example.shubhammishra.notes.Fragments
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.example.shubhammishra.notes.Adapters.SnapsAdapter
import com.example.shubhammishra.notes.Classes.Snaps
import com.example.shubhammishra.notes.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.list_view_camera.view.*

class CameraFragment: android.support.v4.app.Fragment() {
    lateinit var view1: View
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        var listSnaps=ArrayList<Snaps>()
        val cameraAdapter=SnapsAdapter(listSnaps)
        view1.mainViewCamera.layoutManager=StaggeredGridLayoutManager(1,LinearLayoutManager.VERTICAL)
        view1.mainViewCamera.adapter=cameraAdapter
        val auth= FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference=FirebaseDatabase.getInstance().reference.child(auth).child("Snaps")
        databaseReference.addChildEventListener(object:ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val snaps=p0.getValue(Snaps::class.java)
                snaps?.let {
                    listSnaps.add(it)
                    cameraAdapter.notifyDataSetChanged()
                }
            }
            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view1= inflater!!.inflate(R.layout.list_view_camera,null)
        activity!!.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return view1
    }
}