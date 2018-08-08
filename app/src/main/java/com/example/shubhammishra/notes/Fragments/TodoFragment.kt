package com.example.shubhammishra.notes.Fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shubhammishra.notes.R

class TodoFragment: android.support.v4.app.Fragment() {
    lateinit var view1: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view1= inflater!!.inflate(R.layout.list_view_todos,null)
        return view1
    }
}