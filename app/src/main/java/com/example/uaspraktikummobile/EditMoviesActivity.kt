package com.example.uaspraktikummobile

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.uaspraktikummobile.database.Movies
import com.example.uaspraktikummobile.databinding.ActivityEditMoviesBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat

class EditMoviesActivity : AppCompatActivity() {
    private lateinit var bindingEditMovies: ActivityEditMoviesBinding
    private var imageUri: Uri? = null
    private var store: StorageReference? = null
    private var updateId: String = ""
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_movies)

        bindingEditMovies = ActivityEditMoviesBinding.inflate(layoutInflater)
        setContentView(bindingEditMovies.root)

        // buat nampilin film yang diklik
        val selectedMovie = intent.getParcelableExtra<Movies>("SELECTED_MOVIES")
        selectedMovie?.let {
            updateWithSelectedMovie(selectedMovie)
            updateId = selectedMovie.id
        }

        with(bindingEditMovies) {
            ButtonAddMovieImage.setOnClickListener {
                selectImage()
            }
            ButtonConfirmEditMovie.setOnClickListener {
                if (imageUri != null) {
                    // Jika gambar diganti, upload gambar dan update film
                    uploadImageAndUpdateMovie(imageUri!!) { downloadUrl ->
                        updateMovie(
                            Movies(
                                imagePath = downloadUrl,
                                title = EditMovieTitle.text.toString(),
                                director = EditMovieDirector.text.toString(),
                                rating = EditMovieRating.text.toString(),
                                description = EditMovieDescription.text.toString(),
                            )
                        )
                    }
                } else {
                    // Jika gambar tidak diganti, langsung update film tanpa mengunggah gambar
                    updateMovie(
                        Movies(
                            title = EditMovieTitle.text.toString(),
                            director = EditMovieDirector.text.toString(),
                            rating = EditMovieRating.text.toString(),
                            description = EditMovieDescription.text.toString(),
                        )
                    )
                }
            }
            ButtonDeleteMovie.setOnClickListener{
                if (updateId.isNotEmpty()) {
                    val movieCollectionRef = db.collection("movies")
                    val movieRef = movieCollectionRef.document(updateId)

                    // Delete the document
                    movieRef.delete()
                        .addOnSuccessListener {
                            Log.d("EditMoviesActivity", "Movie deleted successfully!")
                            // (Assuming you have stored the image URLs in the "imagePath" field)
                            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(selectedMovie?.imagePath ?: "")
                            storageRef.delete().addOnSuccessListener {
                                Toast.makeText(this@EditMoviesActivity, "Movie deleted successfully!", Toast.LENGTH_SHORT).show()
                                Log.d("EditMoviesActivity", "Movie image deleted successfully!")
                            }.addOnFailureListener { e ->
                                Toast.makeText(this@EditMoviesActivity, "Error deleting movie image", Toast.LENGTH_SHORT).show()
                                Log.w("EditMoviesActivity", "Error deleting movie image", e)
                            }

                            val intent = Intent(this@EditMoviesActivity, MainActivityAdmin::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.w("EditMoviesActivity", "Error deleting movie", e)
                        }
                } else {
                    Log.e("EditMoviesActivity", "Error deleting movie: updateId is empty!")
                }
            }
        }
    }
    // buat ngambil data setelah dipencet
    private fun updateWithSelectedMovie(selectedMovie: Movies) {
        with(bindingEditMovies) {
            EditMovieTitle.setText(selectedMovie.title)
            EditMovieDirector.setText(selectedMovie.director)
            EditMovieDescription.setText(selectedMovie.description)
            EditMovieRating.setText(selectedMovie.rating)

            // Load the image using Glide
            val imagePath = selectedMovie.imagePath
            if (!imagePath.isNullOrEmpty()) {
                Glide.with(this@EditMoviesActivity)
                    .load(imagePath)
                    .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(imageMovie)
            }
        }
    }
    private fun updateMovie(movie: Movies) {
        if (updateId.isNotEmpty()) {
            // Get the document reference for the report to update
            val movieCollectionRef = db.collection("movies")
            val movieRef = movieCollectionRef.document(updateId)

            val updates = mapOf(
                "imagePath" to movie.imagePath,
                "title" to movie.title,
                "director" to movie.director,
                "rating" to movie.rating,
                "description" to movie.description,
            )
            val updatesNoPict = mapOf(
                "title" to movie.title,
                "director" to movie.director,
                "rating" to movie.rating,
                "description" to movie.description,
            )

            // Update the document with the provided data
            if (imageUri != null){
                movieRef.update(updates)
                    .addOnSuccessListener {
                        Log.d("MainActivity", "Movie updated successfully!")
                        val intent = Intent(this@EditMoviesActivity, MainActivityAdmin::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Log.w("MainActivity", "Error updating Movie", e)
                    }
            } else {
                movieRef.update(updatesNoPict)
                    .addOnSuccessListener {
                        Log.d("MainActivity", "Movie updated successfully!")
                        val intent = Intent(this@EditMoviesActivity, MainActivityAdmin::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Log.w("MainActivity", "Error updating Movie", e)
                    }

            }
        } else {
            Log.e("UpdateMovie", "Error updating Movie: updateId is empty!")
        }
    }
    // fungsi terkait image
    private fun selectImage(){
        val intentImage = Intent()
        intentImage.type = "image/*"
        intentImage.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intentImage, 100)
    }
    // preview image dgn requestCode
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == 100 && data != null && data.data != null){
            imageUri = data.data
            bindingEditMovies.imageMovie.setImageURI(imageUri)
        }
    }

    private fun uploadImageAndUpdateMovie(imageUri: Uri, onSuccess: (downloadUrl: String) -> Unit) {
        val progression = android.app.ProgressDialog(this)
        progression.setTitle("Uploading Movie Image...")
        progression.show()

        val sdf = SimpleDateFormat("ddMMyyhhmmss")
        val imageName = sdf.format(System.currentTimeMillis())
        val imageExtension = ".jpg"

        val store = FirebaseStorage.getInstance().getReference("image/$imageName$imageExtension")
        store.putFile(imageUri)
            .addOnSuccessListener {
                bindingEditMovies.imageMovie.setImageURI(null)
                Toast.makeText(this, "Upload Image Success", Toast.LENGTH_SHORT).show()
                store.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    Log.d("Download URL", downloadUrl)
                    onSuccess.invoke(downloadUrl)
                    progression.dismiss()
                }
            }
    }
}