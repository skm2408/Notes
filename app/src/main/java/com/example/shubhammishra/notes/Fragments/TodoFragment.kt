package com.example.shubhammishra.notes.Fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shubhammishra.notes.Classes.GetTodoList
import com.example.shubhammishra.notes.Adapters.TodoListAdapter
import com.example.shubhammishra.notes.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.list_view_todos.view.*

class TodoFragment: android.support.v4.app.Fragment() {
    lateinit var view1: View

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var listTodos=ArrayList<GetTodoList>()
        val adapter=TodoListAdapter(listTodos)
            view1.mainViewTodo.layoutManager=LinearLayoutManager(view1.context)
            view1.mainViewTodo.adapter=adapter
        val userId= FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference=FirebaseDatabase.getInstance().reference.child(userId).child("Todos")
        databaseReference.addChildEventListener(object:ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val todoListList=p0.getValue(GetTodoList::class.java)
                todoListList?.let {
                    listTodos.add(todoListList)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view1= inflater!!.inflate(R.layout.list_view_todos,null)
        return view1
    }
}