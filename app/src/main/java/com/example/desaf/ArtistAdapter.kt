package com.example.desaf

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ArtistAdapter(private val onItemClick: (Artist) -> Unit) : RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {
    private var artists = listOf<Artist>()

    inner class ArtistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.artistName)
        val genre: TextView = itemView.findViewById(R.id.artistGenre)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(artists[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_artist, parent, false)
        return ArtistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        val artist = artists[position]
        holder.name.text = artist.name
        holder.genre.text = artist.genre
    }

    override fun getItemCount() = artists.size

    fun setArtists(newArtists: List<Artist>) {
        artists = newArtists
        notifyDataSetChanged()
    }
}