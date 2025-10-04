package com.example.desaf
// Ubicación: app/src/main/java/com/example/catalogodemusica/SongViewModel.kt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SongViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> = _songs

    fun loadSongs() {
        repository.getAllSongs { songList ->
            _songs.value = songList
        }
    }

    fun addSong(song: Song) {
        repository.addSong(song) { success ->
            if (success) loadSongs()
        }
    }

    fun getSongsByGenre(genre: String) {
        repository.getSongsByGenre(genre) { songList ->
            _songs.value = songList
        }
    }
}
