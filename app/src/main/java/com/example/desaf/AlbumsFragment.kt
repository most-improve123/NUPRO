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

class AlbumsFragment : Fragment() {
    private lateinit var albumViewModel: AlbumViewModel
    private lateinit var albumAdapter: AlbumAdapter
    private lateinit var allAlbumsList: List<Album> // Lista completa para filtro local

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_albums, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        albumViewModel = ViewModelProvider(this).get(AlbumViewModel::class.java)
        allAlbumsList = emptyList()

        // MODIFICADO: Adapter con callback de click
        albumAdapter = AlbumAdapter { album ->
            val intent = Intent(requireContext(), AlbumDetailActivity::class.java)
            intent.putExtra("ALBUM_TITLE", album.title)
            intent.putExtra("ALBUM_ARTIST", album.artist)
            intent.putExtra("ALBUM_YEAR", album.year)
            intent.putExtra("ALBUM_GENRE", album.genre)
            startActivity(intent)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewAlbums)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = albumAdapter

        val searchView = view.findViewById<androidx.appcompat.widget.SearchView>(R.id.searchViewAlbums)
        // USO DEL ID CORRECTO DEL SPINNER
        val genreSpinner = view.findViewById<Spinner>(R.id.genreSpinnerAlbums)
        val fabAddAlbum = view.findViewById<FloatingActionButton>(R.id.fabAddAlbum)

        // LÓGICA DE BÚSQUEDA
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { text ->
                    val filteredAlbums = allAlbumsList.filter {
                        it.title.contains(text, ignoreCase = true) ||
                                it.artist.contains(text, ignoreCase = true)
                    }
                    albumAdapter.setAlbums(filteredAlbums)
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

                val albumsToDisplay = if (selectedGenre == "Todos") {
                    allAlbumsList
                } else {
                    // Filtra por el campo 'genre' del modelo Album
                    allAlbumsList.filter { it.genre.toString().trim().equals(selectedGenre, ignoreCase = true) }
                }

                albumAdapter.setAlbums(albumsToDisplay)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // LÓGICA DEL OBSERVADOR
        albumViewModel.albums.observe(viewLifecycleOwner, { albums ->
            val cleanAlbums = albums.filter { !it.title.isNullOrBlank() } // Limpiar entradas vacías
            allAlbumsList = cleanAlbums // Guarda la lista limpia

            // Refiltramos la lista actual para mantener el estado del Spinner
            val selectedGenre = genres[genreSpinner.selectedItemPosition]
            val albumsToDisplay = if (selectedGenre == "Todos") {
                allAlbumsList
            } else {
                allAlbumsList.filter { it.genre.toString().trim().equals(selectedGenre, ignoreCase = true) }
            }
            albumAdapter.setAlbums(albumsToDisplay)
        })

        fabAddAlbum.setOnClickListener {
            showAddAlbumDialog()
        }

        albumViewModel.loadAlbums()
    }

    private fun showAddAlbumDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_album, null)
        val titleEditText = dialogView.findViewById<EditText>(R.id.titleEditText)
        val artistEditText = dialogView.findViewById<EditText>(R.id.artistEditText)
        val yearEditText = dialogView.findViewById<EditText>(R.id.yearEditText)
        val genreEditText = dialogView.findViewById<EditText>(R.id.genreEditText)

        AlertDialog.Builder(requireContext())
            .setTitle("Agregar Álbum")
            .setView(dialogView)
            .setPositiveButton("Agregar") { _, _ ->
                val album = Album(
                    title = titleEditText.text.toString().trim(),
                    artist = artistEditText.text.toString().trim(),
                    year = yearEditText.text.toString().toIntOrNull() ?: 0,
                    genre = genreEditText.text.toString().trim()
                )
                albumViewModel.addAlbum(album)
                albumViewModel.loadAlbums() // Recarga para actualizar el filtro local
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}