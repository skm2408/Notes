package com.example.shubhammishra.notes.Tables

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.shubhammishra.notes.Adapters.GetTodo

object TodoTable {
    val TableName = "Todo"

    object Columns {
        val id = "ID"
        val task = "Task"
        val done = "Done"
    }

    val create_table = """
        CREATE TABLE $TableName(${Columns.id} INTEGER PRIMARY KEY AUTOINCREMENT,${Columns.task} TEXT,${Columns.done} BOOLEAN);
    """.trimIndent()

    fun insert(db: SQLiteDatabase, task: GetTodo) {
        val row = ContentValues()
        row.put(Columns.task, task.text)
        row.put(Columns.done, task.checked)
        db.insert(TableName, null, row)
    }

    fun delete(db: SQLiteDatabase, task: GetTodo): Int {
        val noOfRows = db.delete(TableName, "${Columns.id}=?", arrayOf(task.id.toString()))
        return noOfRows
    }

    fun update(db: SQLiteDatabase, task: GetTodo) {
        if (task.id == null)
            return
        val row = ContentValues()
        row.put(Columns.task, task.text)
        row.put(Columns.done, task.checked)
        db.update(TableName, row, "${Columns.id}=?", arrayOf("${task.id}"))
    }

    fun getAllTodo(db: SQLiteDatabase): ArrayList<GetTodo> {
        var todoArray = ArrayList<GetTodo>()
        val cursor = db.query(TableName, arrayOf(Columns.id, Columns.task, Columns.done), null, null, null, null, null)
        while (cursor.moveToNext()) {
            todoArray.add(GetTodo(cursor.getInt(0), cursor.getInt(2) == 1, cursor.getString(1)))
        }
        return todoArray
    }
}