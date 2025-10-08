package com.example.desaf

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
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
    // Variable para almacenar la lista completa de canciones una vez cargada de Firebase
    private lateinit var allSongsList: List<Song>

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
        allSongsList = emptyList() // Inicialización segura

        // MODIFICADO: Adapter con callback de click
        songAdapter = SongAdapter { song ->
            val intent = Intent(requireContext(), SongDetailActivity::class.java)
            intent.putExtra("SONG_TITLE", song.title)
            intent.putExtra("SONG_ARTIST", song.artist)
            intent.putExtra("SONG_ALBUM", song.album)
            intent.putExtra("SONG_GENRE", song.genre)
            startActivity(intent)
        }

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
                    // Usamos la lista completa para la búsqueda
                    val filteredSongs = allSongsList.filter {
                        it.title.contains(text, ignoreCase = true) ||
                                it.artist.contains(text, ignoreCase = true)
                    }
                    songAdapter.setSongs(filteredSongs)
                }
                return true
            }
        })

        val genres = listOf("Todos", "Rock", "Pop", "Jazz", "Clásica")

        // CORRECCIÓN 1: Configuración del Spinner para la flechita
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genres)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genreSpinner.adapter = adapter

        // CORRECCIÓN 2: Lógica de filtrado local y tolerable a espacios
        genreSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedGenre = genres[position]

                val songsToDisplay = if (selectedGenre == "Todos") {
                    allSongsList // Si es "Todos", usamos la lista completa que ya cargamos
                } else {
                    // Filtramos la lista completa guardada localmente
                    // Usamos .trim() en el género de la canción por si tiene espacios en Firebase
                    allSongsList.filter { it.genre.toString().trim().equals(selectedGenre, ignoreCase = true) }
                }

                songAdapter.setSongs(songsToDisplay) // Actualizamos el RecyclerView
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

        // CORRECCIÓN 3: Almacenar la lista completa al recibirla y refiltrar
        songViewModel.songs.observe(viewLifecycleOwner, { songs ->
            allSongsList = songs // <-- Guardamos la lista completa, incluyendo la canción nueva

            // Refiltramos la lista actual para mantener el estado del Spinner
            val selectedGenre = genres[genreSpinner.selectedItemPosition]
            val songsToDisplay = if (selectedGenre == "Todos") {
                allSongsList
            } else {
                allSongsList.filter { it.genre.toString().trim().equals(selectedGenre, ignoreCase = true) }
            }
            songAdapter.setSongs(songsToDisplay)
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
                // Normalizamos el texto con .trim() antes de guardar
                val song = Song(
                    title = titleEditText.text.toString().trim(),
                    artist = artistEditText.text.toString().trim(),
                    album = albumEditText.text.toString().trim(),
                    genre = genreEditText.text.toString().trim()
                )

                songViewModel.addSong(song)

                // FORZAMOS la recarga de la lista completa. Esto dispara el 'observe' y actualiza el filtro.
                songViewModel.loadSongs()
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