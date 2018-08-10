package com.example.shubhammishra.notes.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shubhammishra.notes.Classes.GetTodo
import com.example.shubhammishra.notes.R
import kotlinx.android.synthetic.main.list_view_todo.view.*

class TodoAdapter(var todoArray: ArrayList<GetTodo>) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val lf = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return TodoViewHolder(lf.inflate(R.layout.list_view_todo, parent, false))
    }

    override fun getItemCount(): Int = todoArray.size

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.itemView.todoCheckBox.setOnCheckedChangeListener(null)
        holder.itemView.todoCheckBox.isChecked = todoArray[position].checked
        holder.itemView.todoText.text = todoArray[position].text
        holder.itemView.todoCheckBox.setOnCheckedChangeListener({ _, isChecked ->
            todoArray[position].checked = isChecked
        })
        holder.itemView.setOnClickListener({
            todoArray[position].checked = !(holder.itemView.todoCheckBox.isChecked == true)
        })
        holder.itemView.btnCross.setOnClickListener({
            todoArray.removeAt(position)
            notifyDataSetChanged()
        })
    }

    class TodoViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    }
}