package com.example.desaf

// Ubicación: app/src/main/java/com/example/catalogodemusica/ArtistViewModel.kt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ArtistViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    private val _artists = MutableLiveData<List<Artist>>()
    val artists: LiveData<List<Artist>> = _artists

    fun loadArtists() {
        repository.getAllArtists { artistList ->
            _artists.value = artistList
        }
    }

    fun addArtist(artist: Artist) {
        repository.addArtist(artist) { success ->
            if (success) loadArtists()
        }
    }
}
