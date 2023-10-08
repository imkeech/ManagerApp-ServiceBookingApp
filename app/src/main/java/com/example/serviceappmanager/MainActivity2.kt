package com.example.serviceappmanager

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.serviceappmanager.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigationView2.menu.clear()
        binding.bottomNavigationView2.inflateMenu(R.menu.bottom_menu2)

        binding.bottomNavigationView2.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.booked_calls,
                R.id.onhold_calls,
                R.id.completed_calls,
                R.id.cancelled_calls -> {
                    handleFragmentNavigation(menuItem.itemId)
                    true
                }
                else -> {
                    // Handle other menu items if needed
                    false
                }

            }

        }

        // Check if the intent has the fragment name and replace the fragment
        val fragmentName = intent.getStringExtra(MainActivity3.FRAGMENT_KEY)
        Log.d("receivedfragment", "Received fragment name: $fragmentName")
        if (fragmentName != null) {
            handleFragmentNavigation(getMenuItemIdByFragmentName(fragmentName))
        }
    }



    private fun getMenuItemIdByFragmentName(fragmentName: String): Int {
        return when (fragmentName) {
            "BookedFragment" -> R.id.booked_calls
            "OnHoldFragment" -> R.id.onhold_calls
            "CompletedFragment" -> R.id.completed_calls
            "CancelledFragment" -> R.id.cancelled_calls
            else -> -1
        }
    }
    private fun handleFragmentNavigation(itemId: Int) {
        var fragment: Fragment? = null

        when (itemId) {
            R.id.booked_calls -> {
                fragment = BookedFragment()
                Log.d("FragmentNavigation", "Replacing with ${fragment.javaClass.simpleName}")
            }
            R.id.onhold_calls -> {
                fragment = OnHoldFragment()
                Log.d("FragmentNavigation", "Replacing with ${OnHoldFragment.javaClass.simpleName}")
            }
            R.id.completed_calls -> fragment = CompletedFragment()
            R.id.cancelled_calls -> fragment = CancelledFragment()
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
        transaction.add(R.id.frame_layout2, fragment)
        transaction.commitNow()
        Log.d("FragmentNavigation","Transaction done moved to '${fragment.javaClass.name}', isEmpty: ${transaction.isEmpty}")
    }
}
