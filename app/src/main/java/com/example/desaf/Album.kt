package com.example.desaf

data class Album(
    var id: String = "",
    var title: String = "",
    var artist: String = "",
    var year: Int = 0,
    var genre: String = ""
) {
    // Constructor vacío requerido por Firestore
    constructor() : this("", "", "", 0, "")
}