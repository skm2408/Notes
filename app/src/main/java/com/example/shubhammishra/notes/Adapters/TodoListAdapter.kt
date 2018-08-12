package com.example.shubhammishra.notes.Adapters

import android.content.Context
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        holder.itemView.todoToolbarText.text="                    "
        holder.itemView.todoListRecyclerView.layoutManager = LinearLayoutManager(view1.context)
        holder.itemView.todoListRecyclerView.adapter = adapter
        holder.itemView.todoToolbarMenu.setOnClickListener {
            val popupMenu = PopupMenu(holder.itemView.context, holder.itemView.todoToolbarMenu)
            popupMenu.inflate(R.menu.adapter_menu)
            popupMenu.setOnMenuItemClickListener {
                if (it.itemId.equals(R.id.menuEdit)) {
                    alertEditTodos(todoList[position].todoArray,position,holder.itemView.context)
                    true
                } else if (it.itemId.equals(R.id.menuDelete)) {
                    deleteTodos(todoList[position],position)
                    Toast.makeText(holder.itemView.context,"Deleted Successfully",Toast.LENGTH_SHORT).show()
                    true
                } else if (it.itemId.equals(R.id.menuShare)) {
                    shareTodos(todoList[position],holder.itemView)
                    true

                }
                false
            }
            popupMenu.show()
        }
    }

    private fun shareTodos(getTodoList: GetTodoList,view:View) {
           var shareString="Todos:\n"
            for (i in 0..getTodoList.todoArray.size-1)
            {
                shareString+=getTodoList.todoArray[i].text+"\n"
            }
        val intent: Intent = Intent()
        intent.setAction(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, shareString)
        intent.setType("text/plain")
        view.context.startActivity(Intent.createChooser(intent, "Share Via"))
    }

    private fun deleteTodos(getTodoList: GetTodoList, position: Int) {
        val uid= FirebaseAuth.getInstance().currentUser!!.uid
        val databaseRef=FirebaseDatabase.getInstance().reference.child(uid).child("Todos")
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

    private fun alertEditTodos(todoArray: ArrayList<GetTodo>, position: Int, context: Context?) {
        val alertDialog=AlertDialog.Builder(context!!).create()
        alertDialog.setTitle("Edit Box")
        alertDialog.setIcon(R.drawable.menu_edit)
        val lf=context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mView=lf.inflate(R.layout.recycler_view_todos,null)
        alertDialog.setView(mView)
        val adapter = TodoViewAdapter(todoArray)
        mView.todoListRecyclerView.layoutManager = LinearLayoutManager(context)
        mView.todoListRecyclerView.adapter = adapter
        mView.todoToolbar.visibility=View.GONE
        mView.todoListRecyclerView.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(false)
            false
        }
        alertDialog.show()
    }

    inner class TodoListViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    }
}