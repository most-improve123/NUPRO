package com.example.desaf

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SongDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_detail)

        // Recibir los datos del Intent
        val songTitle = intent.getStringExtra("SONG_TITLE") ?: ""
        val artist = intent.getStringExtra("SONG_ARTIST") ?: ""
        val album = intent.getStringExtra("SONG_ALBUM") ?: ""
        val genre = intent.getStringExtra("SONG_GENRE") ?: ""

        // Mostrar los datos en los TextViews
        findViewById<TextView>(R.id.detailTitle).text = "Título: $songTitle"
        findViewById<TextView>(R.id.detailArtist).text = "Artista: $artist"
        findViewById<TextView>(R.id.detailAlbum).text = "Álbum: $album"
        findViewById<TextView>(R.id.detailGenre).text = "Género: $genre"

        // Botón de regresar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle("Detalle de Canción")  // Cambié "title" por "setTitle()"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}