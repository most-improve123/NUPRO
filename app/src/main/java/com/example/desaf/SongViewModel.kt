package com.example.desaf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SongViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> = _songs

    /**
     * Función que siempre carga TODAS las canciones de Firebase.
     */
    fun loadSongs() {
        repository.getAllSongs { songList ->
            _songs.value = songList
        }
    }

    fun addSong(song: Song) {
        // Primero guardamos la canción
        repository.addSong(song) { songSuccess ->
            if (songSuccess) {
                // Si la canción se guardó, creamos el artista
                val artist = Artist(
                    name = song.artist,
                    genre = song.genre,
                    country = "" // Puedes dejarlo vacío o pedir más datos
                )
                repository.addArtist(artist) { artistSuccess ->
                    // Luego creamos el álbum
                    val album = Album(
                        title = song.album,
                        artist = song.artist,
                        year = 0, // Puedes pedir el año después
                        genre = song.genre
                    )
                    repository.addAlbum(album) { albumSuccess ->
                        // Recargamos las canciones (esto dispara el observador en el Fragment)
                        loadSongs()
                    }
                }
            }
        }
    }

    // La función getSongsByGenre ha sido ELIMINADA. El filtrado ahora es local en el Fragment.
}