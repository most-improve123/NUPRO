package com.example.desaf

data class Artist(
    var id: String = "",
    var name: String = "",
    var genre: String = "",
    var country: String = ""
) {
    // Constructor vacío requerido por Firestore
    constructor() : this("", "", "", "")
}