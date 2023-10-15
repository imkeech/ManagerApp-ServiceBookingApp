package com.example.serviceappmanager

import android.content.Intent
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var linearLayout: LinearLayout
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userCollection: CollectionReference
    private val cloudMessaging = CloudMessaging()
    private lateinit var navigate: Button
    private val managerNotifDb = Firebase.firestore.collection("manager-notifications")


    // Store service engineer names and their IDs
    private val serviceEngineerIdMap = mutableMapOf<String, String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        linearLayout = rootView.findViewById(R.id.linearLayout)

        managerNotifDb.get().addOnSuccessListener {
            it.documents.forEach { doc ->

                val cardView = layoutInflater.inflate(
                    R.layout.notification_card, linearLayout, false
                )
                val descreption = cardView.findViewById<TextView>(R.id.notificationDescription)
                descreption.text = doc.getString("description")
                linearLayout.addView(cardView)


            }
        }
        return rootView
    }
/*

        firestore = FirebaseFirestore.getInstance()
        userCollection = firestore.collection("Service_Booking")

        userCollection.get().addOnSuccessListener { querySnapshot ->
                for (documentSnapshot in querySnapshot) {
                    val user = documentSnapshot.toObject(User::class.java)
                    user?.let {
                        val cardView = layoutInflater.inflate(
                            R.layout.card_item_layout, linearLayout, false
                        ) as androidx.cardview.widget.CardView
                        val cardTextView = cardView.findViewById<TextView>(R.id.textViewData)
                        val acceptButton = cardView.findViewById<Button>(R.id.acceptButton)
                        val spinnerServiceEngineer =
                            cardView.findViewById<Spinner>(R.id.spinnerServiceEngineer)

                        val cardText = if (it.problems == "Others") {
                            " Problems: ${it.others}\n\n Machine modelID: ${it.modelId}\n\n"
                        } else {
                            " Problems: ${it.problems}\n\n Machine modelID: ${it.modelId}\n\n"
                        }
                        cardTextView.text = cardText

                        if (it.isAccepted == true) {
                            val tickDrawable = ContextCompat.getDrawable(
                                requireContext(), R.drawable.baseline_check_circle_outline_24
                            )
                            acceptButton.setCompoundDrawablesWithIntrinsicBounds(
                                null, null, tickDrawable, null
                            )
                            acceptButton.text = "Accepted"
                            acceptButton.isEnabled = false
                        } else {
                            acceptButton.setOnClickListener { view ->
                                if (it.isAccepted != true) {
                                    it.isAccepted = true // Mark the item as accepted
                                    val selectedServiceEngineerName =
                                        spinnerServiceEngineer.selectedItem as String
                                    val serviceEngineerId =
                                        serviceEngineerIdMap[selectedServiceEngineerName].toString()
                                            ?: return@setOnClickListener

                                    val updatedFields = hashMapOf<String, Any>(
                                        "accepted" to true, // Only update the isAccepted field
                                        "assignedServiceEngineerId" to serviceEngineerId
                                    )
                                    userCollection.document(documentSnapshot.id)
                                        .update(updatedFields)

                                    // Fetch service engineer details based on the assigned ID
                                    val serviceEngineerCollection =
                                        firestore.collection("ServiceEngineer")
                                    serviceEngineerCollection.whereEqualTo("id", serviceEngineerId)
                                        .get().addOnSuccessListener { querySnapshot ->
                                            for (documentSnapshot in querySnapshot) {
                                                val engineerName =
                                                    documentSnapshot.getString("name")
                                                val engineerPhoneNumber =
                                                    documentSnapshot.getString("phoneNumber")

                                                // Create a message with service engineer details
                                                val message =
                                                    " Booking accepted \n \n $cardText Service Engineer: $engineerName\n \n Phone: $engineerPhoneNumber \n \n"

                                                // Send the message
                                                sendAcceptNotification(
                                                    it.ph_no ?: "", message, serviceEngineerId
                                                )
                                                Log.d("message", "$message")
                                            }
                                        }.addOnFailureListener { e ->
                                            Log.e(
                                                "ServiceEngineer",
                                                "Error fetching service engineer details: ${e.message}"
                                            )
                                        }

                                    val tickDrawable = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.baseline_check_circle_outline_24
                                    )
                                    acceptButton.setCompoundDrawablesWithIntrinsicBounds(
                                        null, null, tickDrawable, null
                                    )
                                    acceptButton.text = "Accepted"
                                    acceptButton.isEnabled = false

                                    it.ph_no?.let { phoneNumber ->
                                        // ... Generate card text ...
                                    }

                                    Toast.makeText(requireContext(), "Accepted", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }

                        // Fetch list of available service engineers
                        val serviceEngineerCollection = firestore.collection("ServiceEngineer")
                        serviceEngineerCollection.whereEqualTo("availability", "Yes").get()
                            .addOnSuccessListener { querySnapshot ->
                                val serviceEngineerList = ArrayList<String>()

                                for (documentSnapshot in querySnapshot) {
                                    val serviceEngineerName = documentSnapshot.getString("name")
                                    val serviceEngineerId = documentSnapshot.getString("id")

                                    serviceEngineerName?.let {
                                        serviceEngineerList.add(it)
                                        serviceEngineerIdMap[it] =
                                            serviceEngineerId.toString() // Store the ID in the map
                                    }
                                }

                                // Create an ArrayAdapter for the Spinner
                                val adapter = ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_spinner_item,
                                    serviceEngineerList
                                )
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                                // Set the ArrayAdapter on the Spinner
                                spinnerServiceEngineer.adapter = adapter
                            }.addOnFailureListener { e ->
                                Log.e(
                                    "ServiceEngineer",
                                    "Error fetching service engineers: ${e.message}"
                                )
                            }

                        // Fetch customer details and append them to cardTextView
                        val customerDetailsCollection = firestore.collection("customerDetails")
                        customerDetailsCollection.whereEqualTo("modelId", it.modelId).get()
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
                            }.addOnFailureListener { e ->
                                Log.e(
                                    "CustomerDetails",
                                    "Error fetching customer details: ${e.message}"
                                )
                            }

                        linearLayout.addView(cardView)
                    }
                }
            }.addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(), "Failed to fetch data: ${e.message}", Toast.LENGTH_SHORT
                ).show()
            }

     */

    private fun sendAcceptNotification(phoneNumber: String, message: String, engineerId: String) {
        cloudMessaging.sendCloudMessage(phoneNumber, message)
        cloudMessaging.sendEngineerNotification(engineerId, phoneNumber)
        Log.d("database", "booking added for number $phoneNumber")
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}
