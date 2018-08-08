package com.example.shubhammishra.notes.Classes

data class UserInfo(var userName:String,var email:String?,var dpUrl:String) {
    constructor():this("","","")
}