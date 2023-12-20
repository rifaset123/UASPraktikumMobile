package com.example.uaspraktikummobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.uaspraktikummobile.database.Movies
import com.example.uaspraktikummobile.databinding.ActivityAccountBinding
import com.example.uaspraktikummobile.helper.Constant
import com.example.uaspraktikummobile.helper.PreferencesHelper
import com.google.firebase.firestore.FirebaseFirestore

class AccountActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var bindingAcc: ActivityAccountBinding
    lateinit var sharedPref: PreferencesHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        bindingAcc = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(bindingAcc.root)


        sharedPref = PreferencesHelper(this)

        val sessionUsername  = sharedPref.getString(Constant.PREF_USERNAME)

        if (sessionUsername != null) {
            val userQuery = db.collection("users")
                .whereEqualTo("username", sessionUsername)
                .get()

            userQuery.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        // ngambil data
                        val userFullName = document.getString("fullname")
                        val userUsername = document.getString("username")
                        val userEmail = document.getString("email")
                        val userStatus = document.getBoolean("isAdmin")

                        with(bindingAcc){
                            TxtFullName.text = userFullName
                            TxtUsername.text = userUsername
                            TxtEmail.text = userEmail

                            if (userStatus == true){
                                TxtStatus.text = "Admin"
                            } else {
                                TxtStatus.text = "User"
                            }
                        }
                    }
                } else {
                    // Handle errors
                    Toast.makeText(this, "Error retrieving data", Toast.LENGTH_SHORT).show()
                }
            }
        }
        bindingAcc.ButtonBack.setOnClickListener{
            val intentToHomepage = Intent(this, MainActivity::class.java)
            startActivity(intentToHomepage)
        }
    }

}