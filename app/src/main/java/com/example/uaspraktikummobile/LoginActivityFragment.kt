package com.example.uaspraktikummobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uaspraktikummobile.adapter.TabAdapter
import com.example.uaspraktikummobile.databinding.ActivityLoginFragmentBinding

class LoginActivityFragment : AppCompatActivity() {
    private lateinit var bindingLogin: ActivityLoginFragmentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_fragment)
        bindingLogin = ActivityLoginFragmentBinding.inflate(layoutInflater)
        setContentView(bindingLogin.root)
        with(bindingLogin) {
            viewPagerLogin.adapter = TabAdapter(supportFragmentManager)
            // Hubungkan ViewPager dengan TabLayout
            tabLayoutLogin.setupWithViewPager(viewPagerLogin)
        }
    }
}
