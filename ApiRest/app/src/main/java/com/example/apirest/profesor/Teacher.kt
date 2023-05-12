package com.example.apirest.profesor

data class Teacher(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val edad: Int,
    val asignatura: String
)