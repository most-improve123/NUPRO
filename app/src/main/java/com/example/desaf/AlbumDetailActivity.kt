package com.example.desaf

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AlbumDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_detail)

        // Recibir los datos del Intent
        val albumTitle = intent.getStringExtra("ALBUM_TITLE") ?: ""
        val artist = intent.getStringExtra("ALBUM_ARTIST") ?: ""
        val year = intent.getIntExtra("ALBUM_YEAR", 0)
        val genre = intent.getStringExtra("ALBUM_GENRE") ?: ""

        // Mostrar los datos
        findViewById<TextView>(R.id.detailAlbumTitle).text = "Título: $albumTitle"
        findViewById<TextView>(R.id.detailAlbumArtist).text = "Artista: $artist"
        findViewById<TextView>(R.id.detailAlbumYear).text = "Año: $year"
        findViewById<TextView>(R.id.detailAlbumGenre).text = "Género: $genre"

        // Botón de regresar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle("Detalle de Álbum")
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}