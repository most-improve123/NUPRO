package com.example.desaf

// Ubicación: app/src/main/java/com/example/catalogodemusica/SongsFragment.kt
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileWriter

class SongsFragment : Fragment() {
    private lateinit var songViewModel: SongViewModel
    private lateinit var songAdapter: SongAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_songs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        songViewModel = ViewModelProvider(this).get(SongViewModel::class.java)
        songAdapter = SongAdapter()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = songAdapter

        val searchView = view.findViewById<androidx.appcompat.widget.SearchView>(R.id.searchView)
        val genreSpinner = view.findViewById<Spinner>(R.id.genreSpinner)
        val fabAddSong = view.findViewById<FloatingActionButton>(R.id.fabAddSong)
        val exportButton = view.findViewById<android.widget.Button>(R.id.exportButton)

        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { text ->
                    songViewModel.songs.value?.let { songs ->
                        val filteredSongs = songs.filter {
                            it.title.contains(text, ignoreCase = true) ||
                                    it.artist.contains(text, ignoreCase = true)
                        }
                        songAdapter.setSongs(filteredSongs)
                    }
                }
                return true
            }
        })

        val genres = listOf("Todos", "Rock", "Pop", "Jazz", "Clásica")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genres)
        genreSpinner.adapter = adapter

        genreSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedGenre = genres[position]
                if (selectedGenre == "Todos") {
                    songViewModel.loadSongs()
                } else {
                    songViewModel.getSongsByGenre(selectedGenre)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        fabAddSong.setOnClickListener {
            showAddSongDialog()
        }

        exportButton.setOnClickListener {
            songViewModel.songs.value?.let { songs ->
                exportSongsToCSV(songs, requireContext())
            }
        }

        songViewModel.songs.observe(viewLifecycleOwner, { songs ->
            songAdapter.setSongs(songs)
        })

        songViewModel.loadSongs()
    }

    private fun showAddSongDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_song, null)
        val titleEditText = dialogView.findViewById<EditText>(R.id.titleEditText)
        val artistEditText = dialogView.findViewById<EditText>(R.id.artistEditText)
        val albumEditText = dialogView.findViewById<EditText>(R.id.albumEditText)
        val genreEditText = dialogView.findViewById<EditText>(R.id.genreEditText)

        AlertDialog.Builder(requireContext())
            .setTitle("Agregar Canción")
            .setView(dialogView)
            .setPositiveButton("Agregar") { _, _ ->
                val song = Song(
                    title = titleEditText.text.toString(),
                    artist = artistEditText.text.toString(),
                    album = albumEditText.text.toString(),
                    genre = genreEditText.text.toString()
                )
                songViewModel.addSong(song)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun exportSongsToCSV(songs: List<Song>, context: Context) {
        val csv = StringBuilder()
        csv.append("Title,Artist,Album,Genre\n")

        songs.forEach { song ->
            csv.append("${song.title},${song.artist},${song.album},${song.genre}\n")
        }

        try {
            val file = File(context.getExternalFilesDir(null), "songs_report.csv")
            FileWriter(file).use { it.write(csv.toString()) }
            Toast.makeText(context, "Reporte generado en $file", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error al generar el reporte", Toast.LENGTH_LONG).show()
        }
    }
}
