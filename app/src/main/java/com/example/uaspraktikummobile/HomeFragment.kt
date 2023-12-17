package com.example.uaspraktikummobile

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uaspraktikummobile.adapter.RvAdminAdapter
import com.example.uaspraktikummobile.adapter.RvMoviesAdapter
import com.example.uaspraktikummobile.adapter.RvMoviesGOATAdapter
import com.example.uaspraktikummobile.database.Movies
import com.example.uaspraktikummobile.databinding.FragmentHomeBinding
import com.example.uaspraktikummobile.helper.Constant
import com.example.uaspraktikummobile.helper.PreferencesHelper
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
    private val GOATMovie = mutableListOf<Movies>() // list untuk menyimpan data yang akan ditampilkan
    private lateinit var rvAdapter: RvMoviesAdapter
    private lateinit var rvAdapterGOAT: RvMoviesGOATAdapter

    lateinit var sharedPref: PreferencesHelper

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


        observeMovies()
        getAllMovies()

        // preference
        sharedPref = PreferencesHelper(requireContext())

        with(bindingPublic){
            // ngambil username dari shared preference
            TxtUsername.text = sharedPref.getString(Constant.PREF_USERNAME)
            // Log the value retrieved from shared preferences
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
}