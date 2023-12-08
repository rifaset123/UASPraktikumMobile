package com.example.uaspraktikummobile

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import com.example.uaspraktikummobile.helper.Constant
import com.example.uaspraktikummobile.helper.PreferencesHelper

class MainActivity : AppCompatActivity() {

    lateinit var sharedPref: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }


}