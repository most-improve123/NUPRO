package com.example.desaf


// Ubicación: app/src/main/java/com/example/catalogodemusica/ArtistsFragment.kt
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ArtistsFragment : Fragment() {
    private lateinit var artistViewModel: ArtistViewModel
    private lateinit var artistAdapter: ArtistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_artists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        artistViewModel = ViewModelProvider(this).get(ArtistViewModel::class.java)
        artistAdapter = ArtistAdapter()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewArtists)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = artistAdapter

        val searchView = view.findViewById<androidx.appcompat.widget.SearchView>(R.id.searchViewArtists)
        val fabAddArtist = view.findViewById<FloatingActionButton>(R.id.fabAddArtist)

        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { text ->
                    artistViewModel.artists.value?.let { artists ->
                        val filteredArtists = artists.filter {
                            it.name.contains(text, ignoreCase = true) ||
                                    it.genre.contains(text, ignoreCase = true)
                        }
                        artistAdapter.setArtists(filteredArtists)
                    }
                }
                return true
            }
        })

        fabAddArtist.setOnClickListener {
            showAddArtistDialog()
        }

        artistViewModel.artists.observe(viewLifecycleOwner, { artists ->
            artistAdapter.setArtists(artists)
        })

        artistViewModel.loadArtists()
    }

    private fun showAddArtistDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_artist, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.nameEditText)
        val genreEditText = dialogView.findViewById<EditText>(R.id.genreEditText)
        val countryEditText = dialogView.findViewById<EditText>(R.id.countryEditText)

        AlertDialog.Builder(requireContext())
            .setTitle("Agregar Artista")
            .setView(dialogView)
            .setPositiveButton("Agregar") { _, _ ->
                val artist = Artist(
                    name = nameEditText.text.toString(),
                    genre = genreEditText.text.toString(),
                    country = countryEditText.text.toString()
                )
                artistViewModel.addArtist(artist)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
