package com.example.serviceappmanager

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.serviceappmanager.databinding.ActivityMain3Binding
import com.google.firebase.messaging.FirebaseMessaging
import fuel.Fuel
import fuel.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class MainActivity3 : AppCompatActivity() {

    private lateinit var binding: ActivityMain3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val analysisUrl = "https://sentanalysis-c0ht.onrender.com/analyze_reviews"

        val reviewText = findViewById<TextView>(R.id.sent_result)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val string = Fuel.get(analysisUrl).body
                val response = JSONObject(string)
                val positives = response.getInt("positive_reviews")
                val negatives = response.getInt("negative_reviews")
                val message = "Positive reviews: $positives Negative reviews: $negatives"
                Log.d("analysis", message)
                reviewText.text = message
            } catch(e: Exception) {
                Log.d("analysis", "The URL is not responding")
            }
        }

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
            intent.putExtra(FRAGMENT_KEY, "SubscriptionFragment")
            startActivity(intent)
        }
        binding.history.setOnClickListener {
            // Navigate to the BookedFragment in MainActivity2
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(FRAGMENT_KEY, "LibraryFragment")
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        askNotificationPermission()
    }

    override fun onResume() {
        super.onResume()
        registerFCM()
    }

    private fun askNotificationPermission() {
        Log.d("fcm", "Asking notification permission")

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("fcm", "Successfully added notifications")
            } else {
                Log.d("fcm", "Failed to get permission for notifications")
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("fcm", "Permission already granted")
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                Log.d("fcm", "Requesting permission since it's a higher API level")
            }
        }
    }

    private fun registerFCM() {
        FirebaseMessaging.getInstance()
            .token
            .addOnSuccessListener {
                Log.d("fcm", "Token: $it")
                FirebaseMessaging.getInstance().subscribeToTopic("booking").addOnSuccessListener {
                    Log.d("fcm", "Subscribed to topic: booking")
                }
            }
    }

    companion object {
        const val FRAGMENT_KEY = "fragment_key"
    }
}
