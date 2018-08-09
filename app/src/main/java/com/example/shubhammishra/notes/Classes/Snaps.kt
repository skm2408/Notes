package com.example.shubhammishra.notes.Classes

data class Snaps(var id:String,var title:String,var description:String,var imgUrl:String){
    constructor():this("","","","")
}