package com.example.uaspraktikummobile

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.uaspraktikummobile.database.Movies
import com.example.uaspraktikummobile.databinding.ActivityAddMoviesBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat

class AddMoviesActivity : AppCompatActivity() {
    private lateinit var bindingAddMovies: ActivityAddMoviesBinding
    private var imageUri: Uri? = null
    private var store: StorageReference? = null
    private val db = FirebaseFirestore.getInstance()


    private val channelId = "NOTIFICATION_CHANNEL_ID"
    private val notifId = 90
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_movies)

        bindingAddMovies = ActivityAddMoviesBinding.inflate(layoutInflater)
        setContentView(bindingAddMovies.root)

        // buat dropdown rating
        val rate = resources.getStringArray(R.array.rate)
        val arrayAdapter = ArrayAdapter(this, R.layout.item_container_rating, rate)
        bindingAddMovies.EditMovieRating.setAdapter(arrayAdapter)

        // buat upload image
        bindingAddMovies.ButtonAddMovieImage.setOnClickListener {
            selectImage()
        }

        with(bindingAddMovies) {
            ButtonSubmitMovie.setOnClickListener {
                if (!isAllFieldsFilled()) {
                    Toast.makeText(this@AddMoviesActivity, "Please fill all fields including image", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                else{
                    uploadData()
                }
            }
        }
    }

    // fungsi terkait CRUD
    private fun isAllFieldsFilled(): Boolean {
        with(bindingAddMovies) {
            return EditMovieTitle.text?.isNotBlank() == true &&
                    EditMovieDirector.text?.isNotBlank() == true &&
                    EditMovieRating.text?.isNotBlank() == true &&
                    EditMovieDescription.text?.isNotBlank() == true &&
                    imageUri != null
        }
    }
    private fun addMovie(movie: Movies) {
        db.collection("movies")
            .add(movie)
            .addOnSuccessListener { documentReference->
                val createdMovieId = documentReference.id
                movie.id = createdMovieId
                documentReference.set(movie)
                    .addOnFailureListener {
                        Log.d("MainActivity", "Error updating movie ID: ", it)
                    }
                val intent = Intent(this@AddMoviesActivity, MainActivityAdmin::class.java)
                startActivity(intent)
                finish()
            }
    }


    // fungsi terkait image
    private fun selectImage(){
        val intentImage = Intent()
        intentImage.type = "image/*"
        intentImage.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intentImage, 100)
    }
    private fun uploadData() {

        // nambahin proses loading
        val progression = android.app.ProgressDialog(this)
        progression.setTitle("Uploading Movie Image...")
        progression.show()

        // untuk nama in imagenya
        val sdf = SimpleDateFormat("ddMMyyhhmmss")
        val imageName = sdf.format(System.currentTimeMillis())
        val imageExtension = ".jpg"

        // buat upload foto
        store = FirebaseStorage.getInstance().getReference("image/$imageName$imageExtension")
        store!!.putFile(imageUri!!)
            .addOnSuccessListener {
                bindingAddMovies.imageMovie.setImageURI(null)
                Toast.makeText(this, "Upload Image Success", Toast.LENGTH_SHORT).show()
                store!!.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    // Now you can use the download URL as needed
                    Log.d("Download URL", downloadUrl.toString())
                    with(bindingAddMovies) {
                        val title = EditMovieTitle.text.toString()
                        val director = EditMovieDirector.text.toString()
                        val description = EditMovieDescription.text.toString()
                        val rating = EditMovieRating.text.toString()
                        val movie = Movies(
                            imagePath = downloadUrl,
                            title = title,
                            director = director,
                            description = description,
                            rating = rating,
                            isTrending = false
                        )
                        addMovie(movie)
                    }
                    progression.dismiss()
                    notifWithImage(downloadUrl)
                }
            }
    }
    // nerima preview image dgn requestCode
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == 100 && data != null && data.data != null){
            imageUri = data.data
            bindingAddMovies.imageMovie.setImageURI(imageUri)
        }
    }

    // notif
    private fun notifWithImage(downloadUrl: String) {
        val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Download the image from Firebase Storage using Glide or any other image loading library
        Glide.with(this@AddMoviesActivity)
            .asBitmap()
            .load(downloadUrl) // Use the downloadUrl here
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    // Build the notification with the downloaded image
                    val builder = NotificationCompat.Builder(this@AddMoviesActivity, channelId)
                        .setSmallIcon(R.drawable.logo_notflix_1)
                        .setContentTitle("Notflix")
                        .setContentText("Movie added successfully")
                        .setStyle(
                            NotificationCompat.BigPictureStyle()
                                .bigPicture(resource)
                        )
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                    // Notify using the NotificationManager
                    notifManager.notify(notifId, builder.build())
                }
            })
    }
}