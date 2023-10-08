package com.example.serviceappmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.serviceappmanager.databinding.ActivityMain3Binding

class MainActivity3 : AppCompatActivity() {

    private lateinit var binding: ActivityMain3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bookedcalls.setOnClickListener {
            // Navigate to the BookedFragment in MainActivity2
            val intent = Intent(this, MainActivity2::class.java)
            intent.putExtra(FRAGMENT_KEY, "BookedFragment")
            startActivity(intent)
        }

        binding.holdcalls.setOnClickListener {
            // Navigate to the BookedFragment in MainActivity2
            val intent = Intent(this, MainActivity2::class.java)
            intent.putExtra(FRAGMENT_KEY, "OnHoldFragment")
            startActivity(intent)
        }

        binding.cancelledcalls.setOnClickListener {
            // Navigate to the BookedFragment in MainActivity2
            val intent = Intent(this, MainActivity2::class.java)
            intent.putExtra(FRAGMENT_KEY, "CancelledFragment")
            startActivity(intent)
        }
        binding.completedcalls.setOnClickListener {
            // Navigate to the BookedFragment in MainActivity2
            val intent = Intent(this, MainActivity2::class.java)
            intent.putExtra(FRAGMENT_KEY, "CompletedFragment")
            startActivity(intent)
        }
        binding.addcustomer.setOnClickListener {
            // Navigate to the BookedFragment in MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(FRAGMENT_KEY, "addFragment")

            startActivity(intent)
        }
        binding.addengineer.setOnClickListener {
            // Navigate to the BookedFragment in MainActivity2
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(FRAGMENT_KEY, "EnginnerFragment")
            startActivity(intent)
        }
        binding.analytics.setOnClickListener {
            // Navigate to the BookedFragment in MainActivity2
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(FRAGMENT_KEY, "LibraryFragment")
            startActivity(intent)
        }
        binding.history.setOnClickListener {
            // Navigate to the BookedFragment in MainActivity2
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(FRAGMENT_KEY, "SubscriptionFragment")
            startActivity(intent)
        }
    }

    companion object {
        const val FRAGMENT_KEY = "fragment_key"
    }
}
