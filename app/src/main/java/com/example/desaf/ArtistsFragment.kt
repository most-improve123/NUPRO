package com.example.desaf

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ArtistsFragment : Fragment() {
    private lateinit var artistViewModel: ArtistViewModel
    private lateinit var artistAdapter: ArtistAdapter
    private lateinit var allArtistsList: List<Artist> // Lista completa para filtro local

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
        allArtistsList = emptyList()

        // MODIFICADO: Adapter con callback de click
        artistAdapter = ArtistAdapter { artist ->
            val intent = Intent(requireContext(), ArtistDetailActivity::class.java)
            intent.putExtra("ARTIST_NAME", artist.name)
            intent.putExtra("ARTIST_GENRE", artist.genre)
            intent.putExtra("ARTIST_COUNTRY", artist.country)
            startActivity(intent)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewArtists)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = artistAdapter

        val searchView = view.findViewById<androidx.appcompat.widget.SearchView>(R.id.searchViewArtists)
        // USO DEL ID CORRECTO DEL SPINNER
        val genreSpinner = view.findViewById<Spinner>(R.id.genreSpinnerArtists)
        val fabAddArtist = view.findViewById<FloatingActionButton>(R.id.fabAddArtist)

        // LÓGICA DE BÚSQUEDA
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { text ->
                    val filteredArtists = allArtistsList.filter {
                        it.name.contains(text, ignoreCase = true) ||
                                it.genre.contains(text, ignoreCase = true)
                    }
                    artistAdapter.setArtists(filteredArtists)
                }
                return true
            }
        })

        // LÓGICA DE FILTRADO POR GÉNERO (Spinner)
        val genres = listOf("Todos", "Rock", "Pop", "Jazz", "Clásica")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genres)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genreSpinner.adapter = adapter

        genreSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedGenre = genres[position]

                val artistsToDisplay = if (selectedGenre == "Todos") {
                    allArtistsList
                } else {
                    // Filtra por el campo 'genre' del modelo Artist
                    allArtistsList.filter { it.genre.toString().trim().equals(selectedGenre, ignoreCase = true) }
                }

                artistAdapter.setArtists(artistsToDisplay)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // LÓGICA DEL OBSERVADOR
        artistViewModel.artists.observe(viewLifecycleOwner, { artists ->
            val cleanArtists = artists.filter { !it.name.isNullOrBlank() } // Limpiar entradas vacías
            allArtistsList = cleanArtists // Guarda la lista limpia

            // Refiltramos la lista actual para mantener el estado del Spinner
            val selectedGenre = genres[genreSpinner.selectedItemPosition]
            val artistsToDisplay = if (selectedGenre == "Todos") {
                allArtistsList
            } else {
                allArtistsList.filter { it.genre.toString().trim().equals(selectedGenre, ignoreCase = true) }
            }
            artistAdapter.setArtists(artistsToDisplay)
        })

        fabAddArtist.setOnClickListener {
            showAddArtistDialog()
        }

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
                    name = nameEditText.text.toString().trim(),
                    genre = genreEditText.text.toString().trim(),
                    country = countryEditText.text.toString().trim()
                )
                artistViewModel.addArtist(artist)
                artistViewModel.loadArtists() // Recarga para actualizar el filtro local
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}