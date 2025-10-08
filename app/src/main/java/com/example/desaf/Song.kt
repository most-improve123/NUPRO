package com.example.desaf

data class Song(
    var id: String = "",
    var title: String = "",
    var artist: String = "",
    var album: String = "",
    var genre: String = ""
) {
    // Constructor vacío requerido por Firestore
    constructor() : this("", "", "", "", "")
}