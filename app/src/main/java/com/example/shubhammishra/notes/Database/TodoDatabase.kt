package com.example.shubhammishra.notes.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.shubhammishra.notes.Tables.TodoTable
import java.sql.SQLException

class TodoDatabase(context: Context) : SQLiteOpenHelper(context, "Notes.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(TodoTable.create_table)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}