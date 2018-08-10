package com.example.shubhammishra.notes.Adapters

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shubhammishra.notes.Classes.GetTodoList
import com.example.shubhammishra.notes.R
import kotlinx.android.synthetic.main.fragment_todos.view.*
import kotlinx.android.synthetic.main.recycler_view_todos.view.*

class TodoListAdapter(var todoList:ArrayList<GetTodoList>): RecyclerView.Adapter<TodoListAdapter.TodoListViewHolder>() {
    lateinit var view1:View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListViewHolder {
        val lf=parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view1=lf.inflate(R.layout.recycler_view_todos,parent,false)
        return TodoListViewHolder(view1)
    }

    override fun getItemCount(): Int=todoList.size

    override fun onBindViewHolder(holder: TodoListViewHolder, position: Int) {
        val adapter=TodoViewAdapter(todoList[position].todoArray)
       holder.itemView.todoListRecyclerView.layoutManager=LinearLayoutManager(view1.context)
        holder.itemView.todoListRecyclerView.adapter=adapter
        holder.itemView.todoListRecyclerView.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
        holder.itemView.todoToolbarMenu.setOnClickListener {
            val popupMenu = PopupMenu(holder.itemView.context, holder.itemView.todoToolbarMenu)
            popupMenu.inflate(R.menu.adapter_menu)
            popupMenu.setOnMenuItemClickListener {
//                if (it.itemId.equals(R.id.menuEdit)) {
//                    alertSnapsEdit(snapList[position],holder.itemView.context,position)
//                    true
//                } else if (it.itemId.equals(R.id.menuDelete)) {
//                    deleteSnaps(snapList[position],position)
//                    Toast.makeText(holder.itemView.context,"Deleted Successfully",Toast.LENGTH_SHORT).show()
//                    true
//                } else if (it.itemId.equals(R.id.menuShare)) {
//                    GetSnaps(snapList[position]).execute()
//                    true
//
//                }
                false
            }
            popupMenu.show()
        }
    }
    inner class TodoListViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    }
}