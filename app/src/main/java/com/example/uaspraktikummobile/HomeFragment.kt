package com.example.uaspraktikummobile

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uaspraktikummobile.adapter.RvMoviesAdapter
import com.example.uaspraktikummobile.adapter.RvMoviesGOATAdapter
import com.example.uaspraktikummobile.database.Movies
import com.example.uaspraktikummobile.database.NetworkStatus
import com.example.uaspraktikummobile.databinding.FragmentHomeBinding
import com.example.uaspraktikummobile.helper.Constant
import com.example.uaspraktikummobile.helper.PreferencesHelper
import com.example.uaspraktikummobile.room.MoviesDao
import com.example.uaspraktikummobile.room.MoviesRoom
import com.example.uaspraktikummobile.room.MoviesRoomDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val db = FirebaseFirestore.getInstance()
    private val moviesCollectionRef = db.collection("movies")
    private lateinit var executorService: ExecutorService
    private lateinit var bindingPublic: FragmentHomeBinding
    private val movieListLiveData: MutableLiveData<List<Movies>> by lazy {
        MutableLiveData<List<Movies>>()
    }
    private val TrendingMovie = mutableListOf<Movies>() // list untuk menyimpan data yang akan ditampilkan
    private val TrendingMovieRoom = mutableListOf<MoviesRoom>() // list untuk menyimpan data yang akan ditampilkan
    private val GOATMovie = mutableListOf<Movies>() // list untuk menyimpan data yang akan ditampilkan
    private val GOATMovieRoom = mutableListOf<MoviesRoom>() // list untuk menyimpan data yang akan ditampilkan
    private lateinit var rvAdapter: RvMoviesAdapter
    private lateinit var rvAdapterGOAT: RvMoviesGOATAdapter

    lateinit var sharedPref: PreferencesHelper

    private lateinit var handler: Handler
    private lateinit var connectionCheckRunnable: Runnable
    private var isConnected: Boolean = false

    // room
    private lateinit var moviesDao: MoviesDao
    private lateinit var moviesRoomDatabase: MoviesRoomDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        executorService = Executors.newSingleThreadExecutor()

        // untuk Trending Movie
        rvAdapter = RvMoviesAdapter(TrendingMovie,
            onItemClick = {movie ->
                executorService.execute {
                    // buat bottom sheet
                    val bottomSheetFragment = BottomSheetFragment.newInstance(movie)
                    bottomSheetFragment.show(requireActivity().supportFragmentManager, bottomSheetFragment.tag)
                }
            },
        )

        rvAdapterGOAT = RvMoviesGOATAdapter(GOATMovie,
            onItemClick = { movie ->
                executorService.execute {
                    // buat bottom sheet
                    val bottomSheetFragment = BottomSheetFragment.newInstance(movie)
                    bottomSheetFragment.show(requireActivity().supportFragmentManager, bottomSheetFragment.tag)
                }
            },
        )

    }
    // mengatur tata letak recyclerview
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // mengaksess recyclerview langsung dari layout
        val recyclerView = view.findViewById<RecyclerView>(R.id.MyRecyclerViewTrending)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = rvAdapter


        val recyclerViewGOAT = view.findViewById<RecyclerView>(R.id.MyRecyclerViewGOAT)
        recyclerViewGOAT.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        recyclerViewGOAT.adapter = rvAdapterGOAT


        // room impement
        moviesRoomDatabase = MoviesRoomDatabase.getDatabase(requireContext())
        moviesDao = moviesRoomDatabase.moviesDao()

        // handling koneksi
        handler = Handler()
        connectionCheckRunnable = object : Runnable {
            override fun run() {
                checkAndHandleConnection()
                handler.postDelayed(this, 2000)
            }
        }

        // preference
        sharedPref = PreferencesHelper(requireContext())

        with(bindingPublic){
            // ngambil username dari shared preference
            TxtUsername.text = sharedPref.getString(Constant.PREF_USERNAME)
            // log
            Log.d("Username", "Retrieved username: ${sharedPref.getString(Constant.PREF_USERNAME)}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // nampilin tampilan fragment
        bindingPublic = FragmentHomeBinding.inflate(inflater, container, false)
        return bindingPublic.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun getAllMovies() {
        observeMoviesChanges()
    }
    private fun observeMovies() {
        movieListLiveData.removeObservers(this) // Hapus observer sebelum menambahkan yang baru
        movieListLiveData.observe(viewLifecycleOwner) { movies->
            TrendingMovie.clear()
            TrendingMovie.addAll(movies)
            rvAdapter.notifyDataSetChanged()


            // clear dahulu sebelum menambahkan yang baru
            GOATMovie.clear()
            GOATMovie.addAll(movies) // Modify this line according to your data source
            rvAdapterGOAT.notifyDataSetChanged()
        }
    }
    private fun observeMoviesChanges() {
        moviesCollectionRef.addSnapshotListener { snapshots, error->
            if (error != null) {
                Log.d("MainActivity", "Error listening for budget changes: ", error)
                return@addSnapshotListener
            }
            val reports = snapshots?.toObjects(Movies::class.java)
            if (reports != null) {
                movieListLiveData.postValue(reports)
            }
        }
    }
    // room
    private fun observeMoviesFromRoom() {
        moviesDao.getAllMovies.observe(viewLifecycleOwner) { movies ->
            updateAdapterData(movies)
        }
    }
    private fun updateAdapterData(roomMovies: List<MoviesRoom>) {
        bindingPublic.root.post {
            // Log TrendingMovieRoom
            val trendingMovies = roomMovies.filter { it.trending }
            TrendingMovieRoom.clear()
            TrendingMovieRoom.addAll(trendingMovies)

            // Log GOATMovieRoom
            val goatMovies = roomMovies.filter { !it.trending }
            GOATMovieRoom.clear()
            GOATMovieRoom.addAll(goatMovies)

            // Merge data from both sources into a single list
            val mergedDataList = mutableListOf<Movies>().apply {
                addAll(TrendingMovieRoom.map { roomMovie ->
                    Movies(
                        id = roomMovie.id.toString(),
                        imagePath = roomMovie.imagePath,
                        title = roomMovie.title,
                        director = roomMovie.director,
                        description = roomMovie.description,
                        rating = roomMovie.rating,
                        isTrending = roomMovie.trending
                    )
                })

                addAll(GOATMovieRoom.map { roomMovie ->
                    Movies(
                        id = roomMovie.id.toString(),
                        imagePath = roomMovie.imagePath,
                        title = roomMovie.title,
                        director = roomMovie.director,
                        description = roomMovie.description,
                        rating = roomMovie.rating,
                        isTrending = roomMovie.trending
                    )
                })
            }

            // observe data agar tampil di recyclerview
            rvAdapter.updateData(mergedDataList)
            rvAdapterGOAT.updateData(mergedDataList)
        }
    }

    // fetching
    private fun fetchMovieRoom() {
        moviesCollectionRef.get().addOnSuccessListener { querySnapshot ->
            val firebaseMovies = querySnapshot.toObjects(Movies::class.java)

            // Convert Firebase movies to Room movies
            val roomMovies = firebaseMovies.map { firebaseMovie ->
                MoviesRoom(
                    id = firebaseMovie.id.toIntOrNull() ?: 0,
                    imagePath = firebaseMovie.imagePath,
                    title = firebaseMovie.title,
                    director = firebaseMovie.director,
                    description = firebaseMovie.description,
                    rating = firebaseMovie.rating,
                    trending = firebaseMovie.isTrending
                )
            }

            // Clear existing data in Room
            executorService.execute {
                // Insert new data to Room
                moviesDao.deleteAllMovies()
                // Insert new data to Room
                moviesDao.insertMovies(roomMovies)

            }
        }.addOnFailureListener { exception ->
            Log.e("MainActivity", "Error fetching movies from Firebase: ", exception)
        }
    }


    private fun checkAndHandleConnection() {
        val isConnectedNow = NetworkStatus.checkConnection(requireContext())

        if (isConnectedNow != isConnected) { // Check if the connection state has changed
            if (isConnectedNow) {
                // Internet connection is now available
                observeMovies()
                getAllMovies()
                fetchMovieRoom()
                sharedPref = PreferencesHelper(requireContext())
                with(bindingPublic) {
                    Toast.makeText(requireContext(), "Connected to Internet! Retrieve Firebase Database...", Toast.LENGTH_SHORT).show()
                    Log.d("Username", "Retrieved username: ${sharedPref.getString(Constant.PREF_USERNAME)}")
                }
            } else {
                // Internet connection is lost
                observeMoviesFromRoom()
                Toast.makeText(requireContext(), "Lost Connection! Switching to Room Database...", Toast.LENGTH_SHORT).show()
                Toast.makeText(requireContext(), "Successfully Retrieved data from Room Database", Toast.LENGTH_SHORT).show()
                Log.d("NetworkStatus", "No internet connection")
            }

            isConnected = isConnectedNow
        }
    }
    override fun onResume() {
        super.onResume()
        // Start the periodic connection check only if the connection is currently lost
        if (!isConnected) {
            handler.post(connectionCheckRunnable)
            observeMoviesFromRoom()
        }
    }

    override fun onPause() {
        super.onPause()
        // Stop the periodic connection check when the fragment is paused
        handler.removeCallbacks(connectionCheckRunnable)
    }

}