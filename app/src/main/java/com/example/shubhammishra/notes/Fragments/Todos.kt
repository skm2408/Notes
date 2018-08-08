package com.example.shubhammishra.notes.Fragments

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shubhammishra.notes.Adapters.GetTodo
import com.example.shubhammishra.notes.Adapters.TodoAdapter
import com.example.shubhammishra.notes.Database.TodoDatabase
import com.example.shubhammishra.notes.R
import com.example.shubhammishra.notes.Tables.TodoTable
import kotlinx.android.synthetic.main.fragment_todos.view.*
import kotlinx.android.synthetic.main.list_view_todo.view.*
import java.sql.SQLException


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
    lateinit var myContext: FragmentActivity
    override fun onAttach(context: Context?) {
        myContext = context as FragmentActivity
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_todos, container, false)
        val todoArray = ArrayList<GetTodo>()
        var adapter: TodoAdapter? = null
        fun RefreshtodoList() {
            val todotable = TodoTable.getAllTodo(db)
            todoArray.clear()
            todoArray.addAll(todotable)
            adapter!!.notifyDataSetChanged()
        }
        adapter = TodoAdapter(todoArray, { task: GetTodo ->
            TodoTable.update(db, task)
            RefreshtodoList()
        }, { task: GetTodo ->
            TodoTable.delete(db, task)
            RefreshtodoList()
        })

        db = TodoDatabase(myContext).writableDatabase
        view.todoAddButton.setOnClickListener({
            val string = view.todoEditText.text.toString()
            if (!string.isEmpty()) {
                val getTodo = GetTodo(null, false, string)
                todoArray.add(getTodo)
                TodoTable.insert(db, getTodo)
                RefreshtodoList()
            }
        })
        view.todoRecyclerView.layoutManager = LinearLayoutManager(view.context)
        view.todoRecyclerView.adapter = adapter
        RefreshtodoList()
        return view
    }

}
