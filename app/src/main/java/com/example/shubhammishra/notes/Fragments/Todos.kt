package com.example.shubhammishra.notes.Fragments

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.shubhammishra.notes.Classes.GetTodo
import com.example.shubhammishra.notes.Classes.GetTodoList
import com.example.shubhammishra.notes.Adapters.TodoAdapter
import com.example.shubhammishra.notes.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_todos.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Todos.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Todos.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class Todos : Fragment() {
    lateinit var db: SQLiteDatabase
    lateinit var view1: View
    lateinit var myContext: FragmentActivity
    override fun onAttach(context: Context?) {
        myContext = context as FragmentActivity
        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var listTodos = ArrayList<GetTodo>()
        var adapter = TodoAdapter(listTodos)
        fun RefreshtodoList() {
            adapter!!.notifyDataSetChanged()
        }
        view1.todoRecyclerView.layoutManager = LinearLayoutManager(view1.context)
        view1.todoRecyclerView.adapter = adapter
        RefreshtodoList()
        view1.todoAddButton.setOnClickListener {
            val string = view1.todoEditText.text.toString()
            if (!string.isEmpty()) {
                val getTodo = GetTodo(false, string)
                listTodos.add(getTodo)
                RefreshtodoList()
            }
        }
        view1.btnSave.setOnClickListener {
            if (!listTodos.isEmpty()) {
                val userId = FirebaseAuth.getInstance().currentUser!!.uid
                val databaseReference = FirebaseDatabase.getInstance().reference.child(userId).child("Todos").push()
                val todoList = GetTodoList(System.currentTimeMillis().toString(), listTodos)
                databaseReference.setValue(todoList)
                Toast.makeText(view1.context, "Saved Successfully", Toast.LENGTH_SHORT).show()
                activity!!.finish()
            } else {
                Toast.makeText(view1.context, "Nothing to Save", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        view1 = inflater.inflate(R.layout.fragment_todos, container, false)
        return view1
    }
}
