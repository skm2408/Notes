package com.example.shubhammishra.notes.Classes

import java.io.Serializable

data class Notes(var id:String,var title:String,var text:String) {
    constructor():this("","","")
}