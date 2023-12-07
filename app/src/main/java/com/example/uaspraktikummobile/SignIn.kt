package com.example.uaspraktikummobile

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uaspraktikummobile.databinding.ActivitySignInBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore

class SignIn : AppCompatActivity() {
    private lateinit var bindingLogin: ActivitySignInBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var UsernameEdit: TextInputEditText
    private lateinit var PasswordEdit: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingLogin = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(bindingLogin.root)

        UsernameEdit = findViewById(R.id.EditUsernameSignIn)
        PasswordEdit= findViewById(R.id.EditPasswordSignIn)

        bindingLogin.ButtonSignIn.setOnClickListener {
            val usr = UsernameEdit.text.toString()
            val pw = PasswordEdit.text.toString()

            if (usr.isEmpty() || pw.isEmpty()) {
                Toast.makeText(this@SignIn, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else {
                // Assume "users" is the collection in your Firestore database
                db.collection("users")
                    .whereEqualTo("username", usr)
                    .whereEqualTo("password", pw)
                    .get()
                    .addOnSuccessListener { task ->
                    if (!task.isEmpty) {
                        // User exists and password matches
                        val intentAdmin = Intent(this@SignIn, MainActivityAdmin::class.java)
                        startActivity(intentAdmin)
                        Toast.makeText(this@SignIn, "Sign In Successfully.", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        // User does not exist or password doesn't match
                        Toast.makeText(this@SignIn, "Sign In Failed, Password doesn't match in our system", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    // Handle failure
                    Toast.makeText(this@SignIn, "Error accessing the database", Toast.LENGTH_SHORT).show()
                }
            }
        }
        bindingLogin.txtSignUp.setOnClickListener {
            val intentSignUp = Intent(this@SignIn, SignUp::class.java)
            startActivity(intentSignUp)
            finish()
        }
    }
}
