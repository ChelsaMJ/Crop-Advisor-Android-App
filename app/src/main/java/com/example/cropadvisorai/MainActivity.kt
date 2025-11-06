package com.example.cropadvisorai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.cropadvisorai.ui.HomeFragment
import com.example.cropadvisorai.ui.AgriChatFragment
import com.example.cropadvisorai.ui.AgriChecklistFragment
import com.example.cropadvisorai.ui.ProfileFragment // Assuming this is simplified

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_host)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Load the initial fragment (The Advisor)
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // Setup Bottom Navigation Listener
        bottomNavigationView.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_chat -> AgriChatFragment()
                R.id.nav_checklist -> AgriChecklistFragment()
                R.id.nav_profile -> ProfileFragment()
                else -> HomeFragment()
            }
            loadFragment(selectedFragment)
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
