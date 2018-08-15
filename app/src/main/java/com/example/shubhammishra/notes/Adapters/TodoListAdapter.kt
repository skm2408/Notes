package com.example.shubhammishra.notes.Adapters

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.example.shubhammishra.notes.Classes.GetTodo
import com.example.shubhammishra.notes.Classes.GetTodoList
import com.example.shubhammishra.notes.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_todos.view.*
import kotlinx.android.synthetic.main.recycler_view_todos.view.*

class TodoListAdapter(var todoList: ArrayList<GetTodoList>) : RecyclerView.Adapter<TodoListAdapter.TodoListViewHolder>() {
    lateinit var view1: View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListViewHolder {
        val lf = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view1 = lf.inflate(R.layout.recycler_view_todos, parent, false)
        return TodoListViewHolder(view1)
    }

    override fun getItemCount(): Int = todoList.size

    override fun onBindViewHolder(holder: TodoListViewHolder, position: Int) {
        val adapter = TodoViewAdapter(todoList[position].todoArray)
        holder.itemView.todoToolbarText.text = "                    "
        holder.itemView.todoListRecyclerView.layoutManager = LinearLayoutManager(view1.context)
        holder.itemView.todoListRecyclerView.adapter = adapter
        holder.itemView.todoToolbarMenu.setOnClickListener {
            val popupMenu = PopupMenu(holder.itemView.context, holder.itemView.todoToolbarMenu)
            popupMenu.inflate(R.menu.adapter_menu)
            popupMenu.setOnMenuItemClickListener {
                if (it.itemId.equals(R.id.menuEdit)) {
                    alertEditTodos(todoList[position].todoArray, position, holder.itemView.context, todoList[position].id)
                    true
                } else if (it.itemId.equals(R.id.menuDelete)) {
                    deleteTodos(todoList[position], position)
                    Toast.makeText(holder.itemView.context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                    true
                } else if (it.itemId.equals(R.id.menuShare)) {
                    shareTodos(todoList[position], holder.itemView)
                    true

                }
                false
            }
            popupMenu.show()
        }
    }

    private fun shareTodos(getTodoList: GetTodoList, view: View) {
        var shareString = "Todos:\n"
        for (i in 0..getTodoList.todoArray.size - 1) {
            shareString += getTodoList.todoArray[i].text + "\n"
        }
        val intent: Intent = Intent()
        intent.setAction(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, shareString)
        intent.setType("text/plain")
        view.context.startActivity(Intent.createChooser(intent, "Share Via"))
    }

    private fun deleteTodos(getTodoList: GetTodoList, position: Int) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseRef = FirebaseDatabase.getInstance().reference.child(uid).child("Todos")
        val query: Query = databaseRef.orderByChild("id").equalTo(getTodoList.id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    if (it.child("id").value!!.equals(getTodoList.id)) {
                        it.ref.removeValue()
                    }
                }
                todoList.removeAt(position)
                notifyDataSetChanged()
            }
        })
    }

    private fun alertEditTodos(todoArray: ArrayList<GetTodo>, position: Int, context: Context?, id: String) {
        val lf = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val mView = lf.inflate(R.layout.recycler_view_todos, null)
        val adapter = TodoEditAdapter(todoArray)
//        mView.todoListRecyclerView.layoutManager = LinearLayoutManager(context)
//        mView.todoListRecyclerView.adapter = adapter
//        mView.todoToolbar.visibility = View.GONE
//        mView.todoListRecyclerView.setOnTouchListener { v, event ->
//            v.parent.requestDisallowInterceptTouchEvent(false)
//            false
//        }
        val mView=lf.inflate(R.layout.fragment_todos,null)
        mView.todoRecyclerView.layoutManager=LinearLayoutManager(context)
        mView.todoRecyclerView.adapter=adapter
        mView.btnSave.visibility=View.GONE
        mView.todoAddButton.setOnClickListener {
            val text=mView.todoEditText.text.toString()
            if (!text.isEmpty())
            {
                todoArray.add(GetTodo(false,text))
                adapter.notifyDataSetChanged()
            }
        }
        val dialog = Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(mView)
//        val params=mView.todoListRecyclerView.layoutParams
//        params.height=3000
//        mView.todoListRecyclerView.layoutParams=params
        dialog.setOnDismissListener {
            val auth = FirebaseAuth.getInstance().currentUser!!.uid
                val dataBaseReference = FirebaseDatabase.getInstance().reference.child(auth).child("Todos")
                val query: Query = dataBaseReference.orderByChild("id").equalTo(id)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        p0.children.forEach {
                            if (it.child("id").value!!.equals(id)) {
                                it.ref.setValue(GetTodoList(id,todoArray))
                            }
                        }
                        todoList[position]=GetTodoList(id,todoArray)
                        notifyDataSetChanged()
                    }
                })
            }
        dialog.show()
    }

    inner class TodoListViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    }
}