package com.example.uaspraktikummobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.uaspraktikummobile.database.Movies
import com.example.uaspraktikummobile.databinding.ActivityMainAdminBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivityAdmin : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val reportCollectionRef = db.collection("movies")
    private lateinit var executorService: ExecutorService
    private lateinit var bindingAdmin: ActivityMainAdminBinding
    private val reportListLiveData: MutableLiveData<List<Movies>> by lazy {
        MutableLiveData<List<Movies>>()
    }
    private val listReports = mutableListOf<Movies>() // Tambahkan list untuk menyimpan data yang akan ditampilkan
    private lateinit var rvAdapter: RvAdminAdapter // Deklarasikan adapter di sini
    private var store: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        executorService = Executors.newSingleThreadExecutor()
        bindingAdmin = ActivityMainAdminBinding.inflate(layoutInflater)
        setContentView(bindingAdmin.root)

        rvAdapter = RvAdminAdapter(listReports,
            onItemClick = { movie ->
                executorService.execute {
                    // Inside onItemClick in your notesAdapter
                    val position = listReports.indexOf(movie)
                    val selectedMovies = listReports[position] // Assuming position is the clicked item position
                    val intent = Intent(this@MainActivityAdmin, EditMoviesActivity::class.java)
                    intent.putExtra("SELECTED_MOVIES", selectedMovies)
                    startActivityForResult(intent, 2)
                }
            },
            onItemLongClick = { movie ->
                deleteMovies(movie = movie) })

        with(bindingAdmin){
            MyRecyclerView.apply {
                layoutManager = GridLayoutManager(context, 2) // mengatur grid
                adapter = rvAdapter
            }
        }

        bindingAdmin.ButtonAddMovie.setOnClickListener {
            val intent = Intent(this, AddMoviesActivity::class.java)
            startActivityForResult(intent, 1)
        }

        observeMovies()
        getAllMovies()
    }


    private fun getAllMovies() {
        observeMoviesChanges()
    }
    private fun observeMovies() {
        reportListLiveData.removeObservers(this) // Hapus observer sebelum menambahkan yang baru
        reportListLiveData.observe(this) { report->
            listReports.clear()
            listReports.addAll(report)
            runOnUiThread {
                rvAdapter.notifyDataSetChanged()
            }
        }
    }
    private fun observeMoviesChanges() {
        reportCollectionRef.addSnapshotListener { snapshots, error->
            if (error != null) {
                Log.d("MainActivity", "Error listening for budget changes: ", error)
                return@addSnapshotListener
            }
            val reports = snapshots?.toObjects(Movies::class.java)
            if (reports != null) {
                reportListLiveData.postValue(reports)
            }
        }
    }
    private fun deleteMovies(movie: Movies) {
        if (movie.id.isEmpty()) {
            Log.d("MainActivity", "Error deleting: movie ID is empty!")
            return
        }
        reportCollectionRef.document(movie.id).delete()
            .addOnFailureListener {
                Log.d("MainActivity", "Error deleting movie: ", it)
            }
    }

//    private fun loadImagesPath() {
//        for (movie in listReports) {
//            val imagePath = movie.imagePath
//            val imageRV: ImageView = findViewById(R.id.imageMovieDisplay)
//
//            // Construct the StorageReference for the image
//            val imageRef = store?.child(imagePath)
//
//            // Download the image and load it into your app (you can use any image loading library here)
//            imageRef?.downloadUrl?.addOnSuccessListener { uri ->
//                // The image URL is available in the 'uri' variable
//                val imageUrl = uri.toString()
//                // Gunakan Glide untuk memuat gambar ke RoundedImageView
//                Glide.with(this)
//                    .load(imageUrl)
//                    .into(imageRV)
//            }?.addOnFailureListener {
//                // Handle failure
//                Log.d("MainActivity", "Error downloading image: $it")
//            }
//        }
//    }
}