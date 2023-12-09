package com.example.uaspraktikummobile

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uaspraktikummobile.database.Movies
import com.google.firebase.storage.StorageReference

class RvAdminAdapter(
    private val reportList: List<Movies>,
    private var store: StorageReference? = null,
    private val onItemClick: (Movies) -> Unit,
    private val onItemLongClick: (Movies) -> Unit
) : RecyclerView.Adapter<RvAdminAdapter.NotesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_container_movies_admin, parent, false)
        return NotesViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val currentReport = reportList[position]
        holder.title.text = currentReport.title
        holder.rating.rating = currentReport.rating.toFloat()
        Glide.with(holder.itemView.context)
            .load(reportList[position].imagePath)
            .into(holder.imageRV)
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.MovieTitle)
        val rating: AppCompatRatingBar = itemView.findViewById(R.id.ratingBar)
        val imageRV: ImageView = itemView.findViewById(R.id.imageMovieDisplay)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(reportList[position])
                }
            }

            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemLongClick(reportList[position])
                    true
                } else {
                    false
                }
            }
        }
    }
}