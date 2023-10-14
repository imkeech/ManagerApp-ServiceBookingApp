package com.example.serviceappmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.example.serviceappmanager.databinding.ActivityMain3Binding
import fuel.Fuel
import fuel.get
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

        runBlocking {
            val string = Fuel.get(analysisUrl).body
            val response = JSONObject(string)
            val positives = response.getInt("positive_reviews")
            val negatives = response.getInt("negative_reviews")
            val message = "Positive reviews: $positives Negative reviews: $negatives"
            Log.d("analysis", message)
            reviewText.text = message
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

    companion object {
        const val FRAGMENT_KEY = "fragment_key"
    }
}
