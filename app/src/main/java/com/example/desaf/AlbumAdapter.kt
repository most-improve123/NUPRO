package com.example.desaf

// Ubicación: app/src/main/java/com/example/catalogodemusica/AlbumAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlbumAdapter : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {
    private var albums = listOf<Album>()

    inner class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.albumTitle)
        val artist: TextView = itemView.findViewById(R.id.albumArtist)
        val year: TextView = itemView.findViewById(R.id.albumYear)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_album, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = albums[position]
        holder.title.text = album.title
        holder.artist.text = album.artist
        holder.year.text = album.year.toString()
    }

    override fun getItemCount() = albums.size

    fun setAlbums(newAlbums: List<Album>) {
        albums = newAlbums
        notifyDataSetChanged()
    }
}
