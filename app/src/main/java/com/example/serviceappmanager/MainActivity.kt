package com.example.serviceappmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.serviceappmanager.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the reference to the FloatingActionButton
        fab = binding.floatingActionButton

        // Set the initial fragment when the activity is created
        //replaceFragment(HomeFragment())

        // Make the bottomNavigationView background transparent
        binding.bottomNavigationView.background = null

        // Set the item selection listener for bottomNavigationView
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.shorts -> replaceFragment(EnginnerFragment())
                R.id.subscriptions -> replaceFragment(SubscriptionFragment())
                R.id.library -> replaceFragment(LibraryFragment())
            }
            true
        }

        // Set the OnClickListener for the FloatingActionButton
        fab.setOnClickListener {
            // Handle FAB click to navigate to NewPageFragment
            val newPageFragment = addFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, newPageFragment)
                .addToBackStack(null) // Optional: Add to back stack if you want back navigation
                .commit()
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }
}
