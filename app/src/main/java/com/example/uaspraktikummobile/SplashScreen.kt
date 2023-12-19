package com.example.uaspraktikummobile

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.uaspraktikummobile.databinding.SplashScreenBinding

class SplashScreen: AppCompatActivity() {
    private lateinit var binding : SplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        supportActionBar?.hide()

        Handler().postDelayed({
            val intentToMainActivity = Intent(this, LoginActivityFragment::class.java)
            startActivity(intentToMainActivity)
            finish()
        }, 1500)
    }
}