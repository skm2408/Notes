package com.example.shubhammishra.notes.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shubhammishra.notes.Classes.GetTodo
import com.example.shubhammishra.notes.R
import kotlinx.android.synthetic.main.list_view_todo.view.*
import kotlinx.android.synthetic.main.todo_list_view.view.*

class TodoEditAdapter(var listTodo:ArrayList<GetTodo>): RecyclerView.Adapter<TodoEditAdapter.TodoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val lf=parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view=lf.inflate(R.layout.list_view_todo,parent,false)
        return TodoViewHolder(view)
    }

    override fun getItemCount(): Int =listTodo.size

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.itemView.todoCheckBox.setOnCheckedChangeListener(null)
        holder.itemView.todoCheckBox.isChecked=listTodo[position].checked
        holder.itemView.todoCheckBox.setOnCheckedChangeListener { _, isChecked ->
            listTodo[position].checked=isChecked
        }
        holder.itemView.todoText.text=listTodo[position].text
        holder.itemView.btnCross.setOnClickListener {
            listTodo.removeAt(position)
            notifyDataSetChanged()
        }
    }

    class TodoViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    }
}