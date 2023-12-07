package com.example.uaspraktikummobile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.example.uaspraktikummobile.databinding.ActivitySignUpBinding
import com.example.uaspraktikummobile.databinding.FragmentSignInBinding
import com.example.uaspraktikummobile.databinding.FragmentSignUpBinding
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignUpFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var bindFragmentSignUp: FragmentSignUpBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var etUsername : EditText
    private lateinit var etFullName : EditText
    private lateinit var etEmail : EditText
    private lateinit var etPassword : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindFragmentSignUp = FragmentSignUpBinding.inflate(inflater, container, false)
        return bindFragmentSignUp.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etFullName = bindFragmentSignUp.EditNameSignUp
        etUsername = bindFragmentSignUp.EditUsernameSignUp
        etEmail = bindFragmentSignUp.EditEmailSignUp
        etPassword = bindFragmentSignUp.EditPasswordSignUp

        with(bindFragmentSignUp) {
            ButtonSignUp.setOnClickListener() {
                if (etUsername.text.toString().isEmpty() || etFullName.text.toString()
                        .isEmpty() || etEmail.text.toString()
                        .isEmpty() || etPassword.text.toString().isEmpty()
                ) {
                    Toast.makeText(
                        requireContext(),
                        "Please fill all the fields",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    db.collection("users")
                        .add(
                            hashMapOf(
                                "username" to etUsername.text.toString(),
                                "fullname" to etFullName.text.toString(),
                                "email" to etEmail.text.toString(),
                                "password" to etPassword.text.toString(),
                                "isAdmin" to "false"
                            )
                        )
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Sign Up Success", Toast.LENGTH_SHORT)
                                .show()
                            val intentSignUp = Intent(requireContext(), SignIn::class.java)
                            etUsername.setText("")
                            etFullName.setText("")
                            etEmail.setText("")
                            etPassword.setText("")
                            startActivity(intentSignUp)
                            requireActivity().finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                requireContext(),
                                "Sign Up Failed, please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
            bindFragmentSignUp.txtSignIn.setOnClickListener {
                // Assuming viewPager is your ViewPager instance
                val viewPager = requireActivity().findViewById<ViewPager>(R.id.viewPagerLogin)

                // Assuming signUpTabIndex is the index of your SignUp tab in the ViewPager
                val signUpTabIndex = 0 // Change this to the correct index

                viewPager.currentItem = signUpTabIndex
            }
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SignUpFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

