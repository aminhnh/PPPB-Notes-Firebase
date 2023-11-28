package com.example.pppbnotesfirebase

data class Note(
    var id : String,
    val title : String,
    val description : String,
    val last_updated_date : String,
    val last_updated_time: String,
)

