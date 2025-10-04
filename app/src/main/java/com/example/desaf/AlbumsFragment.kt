package com.example.desaf


// Ubicación: app/src/main/java/com/example/catalogodemusica/AlbumsFragment.kt
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

class AlbumsFragment : Fragment() {
    private lateinit var albumViewModel: AlbumViewModel
    private lateinit var albumAdapter: AlbumAdapter

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
        albumAdapter = AlbumAdapter()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewAlbums)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = albumAdapter

        val searchView = view.findViewById<androidx.appcompat.widget.SearchView>(R.id.searchViewAlbums)
        val fabAddAlbum = view.findViewById<FloatingActionButton>(R.id.fabAddAlbum)

        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { text ->
                    albumViewModel.albums.value?.let { albums ->
                        val filteredAlbums = albums.filter {
                            it.title.contains(text, ignoreCase = true) ||
                                    it.artist.contains(text, ignoreCase = true)
                        }
                        albumAdapter.setAlbums(filteredAlbums)
                    }
                }
                return true
            }
        })

        fabAddAlbum.setOnClickListener {
            showAddAlbumDialog()
        }

        albumViewModel.albums.observe(viewLifecycleOwner, { albums ->
            albumAdapter.setAlbums(albums)
        })

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
                    title = titleEditText.text.toString(),
                    artist = artistEditText.text.toString(),
                    year = yearEditText.text.toString().toIntOrNull() ?: 0,
                    genre = genreEditText.text.toString()
                )
                albumViewModel.addAlbum(album)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
