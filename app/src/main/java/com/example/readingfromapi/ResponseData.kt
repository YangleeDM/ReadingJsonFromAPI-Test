package com.example.readingfromapi

/* Creando data class para almacenar mis elementos de mi objeto tipo Json */

data class ResponseData (
    val id:Int,
    val nombre:String,
    val edad:Int,
    val email:String
)