package com.example.uaspraktikummobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.uaspraktikummobile.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class SignUp : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var etUsername : EditText
    private lateinit var etFullName : EditText
    private lateinit var etEmail : EditText
    private lateinit var etPassword : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        etFullName = findViewById(R.id.EditNameSignUp)
        etUsername = findViewById(R.id.EditUsernameSignUp)
        etEmail = findViewById(R.id.EditEmailSignUp)
        etPassword = findViewById(R.id.EditPasswordSignUp)

        with(binding){
            ButtonSignUp.setOnClickListener(){
                if (etUsername.text.toString().isEmpty() || etFullName.text.toString().isEmpty() || etEmail.text.toString().isEmpty() || etPassword.text.toString().isEmpty()){
                    Toast.makeText(this@SignUp, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                }else {
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
                            Toast.makeText(this@SignUp, "Sign Up Success", Toast.LENGTH_SHORT).show()
                            val intentSignUp = Intent(this@SignUp, SignIn::class.java)
                            etUsername.setText("")
                            etFullName.setText("")
                            etEmail.setText("")
                            etPassword.setText("")
                            startActivity(intentSignUp)
                            finish()

                        }
                        .addOnFailureListener {
                            Toast.makeText(this@SignUp, "Sign Up Failed, please try again.", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            txtSignIn.setOnClickListener {
                val intentSignUp = Intent(this@SignUp, SignIn::class.java)
                startActivity(intentSignUp)
                finish()
            }
        }
    }
}