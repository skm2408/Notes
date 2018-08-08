package com.example.shubhammishra.notes.Classes

data class Recording(var id:String,var recordName:String,var recordUrl:String) {
    constructor():this("","","")
}