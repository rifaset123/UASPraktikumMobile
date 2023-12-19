package com.example.uaspraktikummobile.adapter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uaspraktikummobile.R
import com.example.uaspraktikummobile.database.Movies
import com.example.uaspraktikummobile.room.MoviesRoom

class RvMoviesGOATAdapter (
    private var listMovie: List<Movies>,
    private val onItemClick: (Movies) -> Unit,
    ) : RecyclerView.Adapter<RvMoviesGOATAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_container_movies_goat, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val currentReport = listMovie[position]
        holder.title.text = currentReport.title
        holder.rating.rating = currentReport.rating.toFloat()
        Glide.with(holder.itemView.context)
            .load(listMovie[position].imagePath)
            .into(holder.imageRV)

    }

    override fun getItemCount(): Int {
        return listMovie.size
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.MovieTitle)
        val rating: AppCompatRatingBar = itemView.findViewById(R.id.ratingBar)
        val imageRV: ImageView = itemView.findViewById(R.id.imageMovieDisplay)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(listMovie[position])
                }
            }
        }
    }
    fun updateData(newMovies: List<Movies>) {
        listMovie = newMovies
        notifyDataSetChanged()
    }
}