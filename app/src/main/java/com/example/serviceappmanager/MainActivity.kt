package com.example.serviceappmanager

import android.os.Bundle
import android.util.Log
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

        // Make the bottomNavigationView background transparent
        binding.bottomNavigationView.background = null

        // Set the item selection listener for bottomNavigationView
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.shorts -> replaceFragment(EnginnerFragment())
                R.id.subscriptions -> replaceFragment(SubscriptionFragment())
                R.id.library -> replaceFragment(LibraryFragment())
                R.id.floatingActionButton -> replaceFragment(addFragment())
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

        // Check if the intent has the fragment name and replace the fragment
        val fragmentName = intent.getStringExtra(MainActivity3.FRAGMENT_KEY)
        if (fragmentName != null) {
            handleFragmentNavigation(getMenuItemIdByFragmentName(fragmentName))
        }
    }

    private fun getMenuItemIdByFragmentName(fragmentName: String): Int {
        // Mapping fragment names to menu item IDs
        return when (fragmentName) {
            "HomeFragment" -> R.id.home
            "EnginnerFragment" -> R.id.shorts
            "SubscriptionFragment" -> R.id.subscriptions
            "LibraryFragment" -> R.id.library
            "addFragment" -> R.id.floatingActionButton
            else -> -1
        }
    }

    private fun handleFragmentNavigation(itemId: Int) {
        var fragment: Fragment? = null

        when (itemId) {
            R.id.home -> fragment = HomeFragment()
            R.id.shorts -> fragment = EnginnerFragment()
            R.id.subscriptions -> fragment = SubscriptionFragment()
            R.id.library -> fragment = LibraryFragment()
            R.id.floatingActionButton -> fragment = addFragment()
            else -> {
                // Handle other menu items if needed
            }
        }
        replaceFragment(fragment)
    }

    private fun replaceFragment(fragment: Fragment?) {
        if (fragment == null) {
            Log.d("FragmentNavigation", "Unknown fragment was sent")
            return
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, fragment)
        transaction.commitNow()
        Log.d("FragmentNavigation", "Transaction done moved to '${fragment.javaClass.name}', isEmpty: ${transaction.isEmpty}")
    }
}
