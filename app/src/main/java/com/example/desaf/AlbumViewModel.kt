package com.example.desaf

// Ubicación: app/src/main/java/com/example/catalogodemusica/AlbumViewModel.kt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AlbumViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>> = _albums

    fun loadAlbums() {
        repository.getAllAlbums { albumList ->
            _albums.value = albumList
        }
    }

    fun addAlbum(album: Album) {
        repository.addAlbum(album) { success ->
            if (success) loadAlbums()
        }
    }
}
