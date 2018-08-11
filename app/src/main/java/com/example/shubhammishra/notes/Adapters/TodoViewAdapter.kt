package com.example.shubhammishra.notes.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shubhammishra.notes.Classes.GetTodo
import com.example.shubhammishra.notes.R
import kotlinx.android.synthetic.main.todo_list_view.view.*

class TodoViewAdapter(var listTodo:ArrayList<GetTodo>): RecyclerView.Adapter<TodoViewAdapter.TodoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val lf=parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return TodoViewHolder(lf.inflate(R.layout.todo_list_view,parent,false))
    }

    override fun getItemCount(): Int =listTodo.size

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.itemView.todoCheck.isChecked=listTodo[position].checked
        holder.itemView.todoTask.text=listTodo[position].text
        holder.itemView.todoCheck.isClickable=false
    }

    inner class TodoViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    }
}