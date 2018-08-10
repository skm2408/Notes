package com.example.shubhammishra.notes.Classes

data class GetTodo(var checked:Boolean, val text:String)
{
    constructor():this(false,"")
}
data class GetTodoList(var id:String,var todoArray:ArrayList<GetTodo>)
{
    constructor():this("",ArrayList())
}