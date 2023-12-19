package com.example.uaspraktikummobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRatingBar
import com.bumptech.glide.Glide
import com.example.uaspraktikummobile.database.Movies
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFragment : BottomSheetDialogFragment(){
    companion object {
        const val ARG_SELECTED_MOVIE = "ARG_SELECTED_MOVIE"

        fun newInstance(selectedMovie: Movies): BottomSheetFragment {
            val fragment = BottomSheetFragment()
            val args = Bundle().apply {
                putParcelable(ARG_SELECTED_MOVIE, selectedMovie)
            }
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_fragment, container, false)
    }

    // fungsionality
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the movie object from arguments
        val selectedMovie = arguments?.getParcelable<Movies>(ARG_SELECTED_MOVIE)

        // Use the selectedMovie object to populate your BottomSheet UI
        if (selectedMovie != null) {
            // Populate your BottomSheet UI with the selected movie details
            // Example: display movie title in a TextView
            val titleTextView: TextView = view.findViewById(R.id.TxtMovieTitle)
            val directorTextView: TextView = view.findViewById(R.id.TxtDirector)
            val storyTextView: TextView = view.findViewById(R.id.TxtStoryline)
            val itemRating: AppCompatRatingBar = view.findViewById(R.id.ratingBar1)
            val image: ImageView = view.findViewById(R.id.imageMovie)

            // ngambil dari parcellable movies
            titleTextView.text = selectedMovie.title
            directorTextView.text = selectedMovie.director
            storyTextView.text = selectedMovie.description
            itemRating.rating = selectedMovie.rating.toFloat()
            Glide.with(this)
                .load(selectedMovie.imagePath)
                .into(image)
        }
    }

}