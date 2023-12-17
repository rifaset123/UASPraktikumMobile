package com.example.uaspraktikummobile

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import com.example.uaspraktikummobile.databinding.ActivityMainBinding
import com.example.uaspraktikummobile.helper.Constant
import com.example.uaspraktikummobile.helper.PreferencesHelper

class MainActivity : AppCompatActivity() {

    lateinit var sharedPref: PreferencesHelper
    private lateinit var bindingPublicFragment: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindingPublicFragment = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingPublicFragment.root)
        loadFragment(HomeFragment())

        with(bindingPublicFragment){
            bottomNavigationView.setOnItemSelectedListener {
                when(it.itemId){
                    R.id.homeFragment-> {
                        loadFragment(HomeFragment())
                        true
                    }
                    R.id.settingFragment -> {
                        loadFragment(SettingsFragment())
                        true
                    }
                    else ->{
                        false}
                }
            }
        }
    }
    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}