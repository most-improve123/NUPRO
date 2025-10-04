package com.example.desaf

// Ubicación: app/src/main/java/com/example/catalogodemusica/FirebaseRepository.kt
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()

    // Funciones para canciones
    fun addSong(song: Song, callback: (Boolean) -> Unit) {
        db.collection("songs")
            .add(song)
            .addOnSuccessListener { documentReference ->
                db.collection("songs").document(documentReference.id)
                    .set(song.copy(id = documentReference.id))
                    .addOnSuccessListener { callback(true) }
                    .addOnFailureListener { callback(false) }
            }
            .addOnFailureListener { callback(false) }
    }

    fun getAllSongs(callback: (List<Song>) -> Unit) {
        db.collection("songs")
            .get()
            .addOnSuccessListener { result ->
                val songs = result.toObjects(Song::class.java)
                callback(songs)
            }
            .addOnFailureListener { callback(emptyList()) }
    }

    fun getSongsByGenre(genre: String, callback: (List<Song>) -> Unit) {
        db.collection("songs")
            .whereEqualTo("genre", genre)
            .get()
            .addOnSuccessListener { result ->
                val songs = result.toObjects(Song::class.java)
                callback(songs)
            }
            .addOnFailureListener { callback(emptyList()) }
    }

    // Funciones para álbumes
    fun addAlbum(album: Album, callback: (Boolean) -> Unit) {
        db.collection("albums")
            .add(album)
            .addOnSuccessListener { documentReference ->
                db.collection("albums").document(documentReference.id)
                    .set(album.copy(id = documentReference.id))
                    .addOnSuccessListener { callback(true) }
                    .addOnFailureListener { callback(false) }
            }
            .addOnFailureListener { callback(false) }
    }

    fun getAllAlbums(callback: (List<Album>) -> Unit) {
        db.collection("albums")
            .get()
            .addOnSuccessListener { result ->
                val albums = result.toObjects(Album::class.java)
                callback(albums)
            }
            .addOnFailureListener { callback(emptyList()) }
    }

    // Funciones para artistas
    fun addArtist(artist: Artist, callback: (Boolean) -> Unit) {
        db.collection("artists")
            .add(artist)
            .addOnSuccessListener { documentReference ->
                db.collection("artists").document(documentReference.id)
                    .set(artist.copy(id = documentReference.id))
                    .addOnSuccessListener { callback(true) }
                    .addOnFailureListener { callback(false) }
            }
            .addOnFailureListener { callback(false) }
    }

    fun getAllArtists(callback: (List<Artist>) -> Unit) {
        db.collection("artists")
            .get()
            .addOnSuccessListener { result ->
                val artists = result.toObjects(Artist::class.java)
                callback(artists)
            }
            .addOnFailureListener { callback(emptyList()) }
    }
}
