package com.example.uaspraktikummobile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.uaspraktikummobile.databinding.FragmentSettingsBinding
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
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val db = FirebaseFirestore.getInstance()
    private val moviesCollectionRef = db.collection("movies")
    private lateinit var executorService: ExecutorService
    private lateinit var bindingSettings: FragmentSettingsBinding
    lateinit var sharedPref: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        executorService = Executors.newSingleThreadExecutor()
        bindingSettings = FragmentSettingsBinding.inflate(layoutInflater)

        sharedPref = PreferencesHelper(requireContext())

        with(bindingSettings){
            // ngambil username dari shared preference
            ButtonLogout.setOnClickListener{
                ButtonLogout.isClickable = true
                ButtonLogout.isEnabled = true

                sharedPref.clear()
                Toast.makeText(requireContext(), "Logout Success", Toast.LENGTH_SHORT).show()
                val intentLogOut = Intent(requireContext(), LoginActivityFragment::class.java)
                startActivity(intentLogOut)
            }

            ButtonAboutUs.setOnClickListener{
                ButtonAboutUs.isClickable = true
                ButtonAboutUs.isEnabled = true
                val intentAccount = Intent(requireContext(), AboutUsActivity::class.java)
                startActivity(intentAccount)
            }

            ButtonAccount.setOnClickListener{
                ButtonAccount.isClickable = true
                ButtonAccount.isEnabled = true
                val intentAccount = Intent(requireContext(), AccountActivity::class.java)
                startActivity(intentAccount)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // nampilin tampilan fragment
        return bindingSettings.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}