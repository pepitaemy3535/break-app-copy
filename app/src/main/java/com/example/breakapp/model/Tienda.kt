package com.example.breakapp.model

import com.google.firebase.firestore.DocumentId

data class Tienda(
    @DocumentId val id: String = "",
    val nombre: String = "",
    val calificacion: Double = 0.0,
    val horario: String = ""
)