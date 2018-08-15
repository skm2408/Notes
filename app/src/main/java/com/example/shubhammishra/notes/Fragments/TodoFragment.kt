package com.example.shubhammishra.notes.Fragments

import android.os.Build
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shubhammishra.notes.Classes.GetTodoList
import com.example.shubhammishra.notes.Adapters.TodoListAdapter
import com.example.shubhammishra.notes.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.list_view_notes.view.*
import kotlinx.android.synthetic.main.list_view_todos.view.*

class TodoFragment : android.support.v4.app.Fragment() {
    lateinit var view1: View
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var listTodos = ArrayList<GetTodoList>()
        val adapter = TodoListAdapter(listTodos)
        view1.mainViewTodo.layoutManager = GridLayoutManager(view1.context, 2)
        view1.mainViewTodo.adapter = adapter
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference = FirebaseDatabase.getInstance().reference.child(userId).child("Todos")
        databaseReference.keepSynced(true)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val todos = p0.getValue(GetTodoList::class.java)
                if (todos == null) {
                    view1.mainViewTodo.visibility = View.GONE
                    view1.empty_view_todos.visibility = View.VISIBLE
                    view1.tvMessage_todos.text = "Nothing to Display Yet\nAdded Todos will be displayed here"
                } else {
                    view1.mainViewTodo.visibility = View.VISIBLE
                    view1.empty_view_todos.visibility = View.GONE
                }
            }
        })

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                if (listTodos.isEmpty()) {
                    view1.mainViewTodo.visibility = View.GONE
                    view1.empty_view_todos.visibility = View.VISIBLE
                    view1.tvMessage_todos.text = "Nothing to Display Yet\nAdded Todos will be displayed here"
                } else {
                    view1.mainViewTodo.visibility = View.VISIBLE
                    view1.empty_view_todos.visibility = View.GONE
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
                val todoListList = p0.getValue(GetTodoList::class.java)
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
        view1 = inflater!!.inflate(R.layout.list_view_todos, null)
        return view1
    }
}