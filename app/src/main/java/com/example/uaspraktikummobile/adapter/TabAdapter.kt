package com.example.uaspraktikummobile.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.uaspraktikummobile.SignInFragment
import com.example.uaspraktikummobile.SignUpFragment

class TabAdapter (fm: FragmentManager) : FragmentPagerAdapter(fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    // menerima parameter position yang merupakan indeks tab yang dipilih
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> SignInFragment()
            1 -> SignUpFragment()
            else -> throw IllegalArgumentException("Invalid tab position")
        }
    }
    // mengembalikan jumlah tab yang tersedia
    override fun getCount(): Int {
        return 2
    }
    // menerima parameter position yang sama dengan method getItem
    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Sign In"
            1 -> "Sign Up"
            else -> null
        }
    }
}