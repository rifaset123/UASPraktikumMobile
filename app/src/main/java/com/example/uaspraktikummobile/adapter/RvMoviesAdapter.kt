package com.example.uaspraktikummobile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uaspraktikummobile.R
import com.example.uaspraktikummobile.database.Movies
import com.google.firebase.firestore.FirebaseFirestore

class RvMoviesAdapter(
    private val listMovie: List<Movies>,
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val onItemClick: (Movies) -> Unit,
) : RecyclerView.Adapter<RvMoviesAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_container_movies_trending, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val currentMovie = listMovie[position]

        // Nampilin trending movie
        if (currentMovie.isTrending) {
            Glide.with(holder.itemView.context)
                .load(currentMovie.imagePath)
                .into(holder.imageRV)
            // biar nge wrap gambarnya
            holder.itemView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        } else {
            // ngilangin gambar yang gak trending biar gak ghosting
            holder.itemView.layoutParams.width = 0
        }
    }

    override fun getItemCount(): Int {
        return listMovie.size
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val title: TextView = itemView.findViewById(R.id.MovieTitle)
//        val rating: AppCompatRatingBar = itemView.findViewById(R.id.ratingBar)
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
}