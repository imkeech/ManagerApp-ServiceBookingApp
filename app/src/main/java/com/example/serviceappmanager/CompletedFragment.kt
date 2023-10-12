package com.example.serviceappmanager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class CompletedFragment : Fragment() {
    private lateinit var linearLayout: LinearLayout
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userCollection: CollectionReference
    private val cloudMessaging = CloudMessaging()

    // Store service engineer names and their IDs
    private val serviceEngineerIdMap = mutableMapOf<String, String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_cancelled, container, false)

        linearLayout = rootView.findViewById(R.id.linearLayoutcancelled)
        firestore = FirebaseFirestore.getInstance()
        userCollection = firestore.collection("Service_Booking")

        userCollection.whereEqualTo("callStatus", "Completed")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (documentSnapshot in querySnapshot) {
                    val user = documentSnapshot.toObject(User::class.java)
                    var call = documentSnapshot.getString("callStatus")

                    user?.let {
                        val cardView = layoutInflater.inflate(
                            R.layout.card_item_layout2,
                            linearLayout,
                            false
                        ) as androidx.cardview.widget.CardView
                        val cardTextView = cardView.findViewById<TextView>(R.id.textViewData)

                        val cardText =
                            if (it.problems == "Others") {
                                " Problems: ${it.others}\n\n Machine modelID: ${it.modelId}\n\n callStatus : $call \n\n"
                            } else {
                                " Problems: ${it.problems}\n\n Machine modelID: ${it.modelId}\n\n callStatus : $call \n\n"
                            }
                        cardTextView.text = cardText

                        // Fetch list of available service engineers
                        val serviceEngineerCollection = firestore.collection("ServiceEngineer")
                        serviceEngineerCollection.whereEqualTo("availability", "Yes")
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                val serviceEngineerList = ArrayList<String>()

                                for (documentSnapshot in querySnapshot) {
                                    val serviceEngineerName = documentSnapshot.getString("name")
                                    val serviceEngineerId = documentSnapshot.getString("id")

                                    serviceEngineerName?.let {
                                        serviceEngineerList.add(it)
                                        serviceEngineerIdMap[it] = serviceEngineerId.toString() // Store the ID in the map
                                    }
                                }

                                // Create an ArrayAdapter for the Spinner
                                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, serviceEngineerList)
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                                // Set the ArrayAdapter on the Spinner

                            }
                            .addOnFailureListener { e ->
                                Log.e("ServiceEngineer", "Error fetching service engineers: ${e.message}")
                            }

                        // Fetch customer details and append them to cardTextView
                        val customerDetailsCollection = firestore.collection("customerDetails")
                        customerDetailsCollection.whereEqualTo("modelId", it.modelId)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                val customerDetailsText = StringBuilder()
                                val uniqueModelIds = HashSet<String>()

                                for (document in querySnapshot) {
                                    val modelId = document.getString("modelId")

                                    // Check if the modelId is unique
                                    if (!uniqueModelIds.contains(modelId)) {
                                        val name = document.getString("name")
                                        val address = document.getString("address")
                                        val ph_no = document.getString("ph_no")
                                        val amd = document.getString("AMD")

                                        customerDetailsText.append(" Customer Name: $name\n\n Address: $address\n\n Phone: $ph_no\n\n AMD: $amd")
                                        if (modelId != null) {
                                            uniqueModelIds.add(modelId)
                                        }
                                    }
                                }

                                cardTextView.append(customerDetailsText.toString())
                            }
                            .addOnFailureListener { e ->
                                Log.e("CustomerDetails", "Error fetching customer details: ${e.message}")
                            }

                        linearLayout.addView(cardView)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Failed to fetch data: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance() = OnHoldFragment()
    }
}
