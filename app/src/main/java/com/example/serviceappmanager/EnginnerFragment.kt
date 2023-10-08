package com.example.serviceappmanager

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class EnginnerFragment : Fragment() {

    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var ph_no: EditText
    private lateinit var state: EditText
    private lateinit var city: EditText
    private lateinit var address: EditText
    private lateinit var joiningdate: EditText
    private lateinit var availability: AutoCompleteTextView
    private lateinit var empId: EditText
    private lateinit var btnInsertData: Button
    private lateinit var firestore: FirebaseFirestore
    private lateinit var engineerCollection: CollectionReference
    private lateinit var selectedJoiningDate: String

    private val availabilityOptions = arrayOf("Yes", "No")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_engineer, container, false)

        name = view.findViewById(R.id.name)
        email = view.findViewById(R.id.email)
        ph_no = view.findViewById(R.id.number)
        state = view.findViewById(R.id.state)
        city = view.findViewById(R.id.city)
        address = view.findViewById(R.id.address)
        joiningdate = view.findViewById(R.id.joiningdate)
        availability = view.findViewById(R.id.availability)
        empId = view.findViewById(R.id.id)
        btnInsertData = view.findViewById(R.id.add)

        // Initialize Firestore reference
        firestore = FirebaseFirestore.getInstance()
        engineerCollection = firestore.collection("ServiceEngineer")

        // Set up ArrayAdapter for AutoCompleteTextView
        val availabilityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, availabilityOptions)
        availability.setAdapter(availabilityAdapter)

        joiningdate.setOnClickListener {
            showDatePickerDialog()
        }

        btnInsertData.setOnClickListener {
            insertEngineerData()
        }

        return view
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, pickedYear, pickedMonth, pickedDay ->
                val formattedDate = String.format("%04d-%02d-%02d", pickedYear, pickedMonth + 1, pickedDay)
                selectedJoiningDate = formattedDate
                joiningdate.setText(formattedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun insertEngineerData() {
        val engineerName = name.text.toString()
        val engineerEmail = email.text.toString()
        val engineerPhone = "+91" + ph_no.text.toString()
        val engineerState = state.text.toString()
        val engineerCity = city.text.toString()
        val engineerAddress = address.text.toString()
        val engineerAvailability = availability.text.toString()
        val engineerEmpId = empId.text.toString()

        if (engineerName.isEmpty() || engineerEmail.isEmpty() || engineerPhone.isEmpty() ||
            engineerState.isEmpty() || engineerCity.isEmpty() || engineerAddress.isEmpty() ||
            engineerAvailability.isEmpty() || engineerEmpId.isEmpty() || selectedJoiningDate.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate AMD value
        if (engineerAvailability != "Yes" && engineerAvailability != "No") {
            Toast.makeText(requireContext(), "Please enter 'Yes' or 'No' for Availability.", Toast.LENGTH_SHORT).show()
            return
        }

        // Generate the password
        val password = generatePassword(engineerName, selectedJoiningDate)

        val engineer = ServiceEngineer(
            engineerName,
            engineerEmail,
            engineerPhone,
            engineerState,
            engineerCity,
            engineerAddress,
            selectedJoiningDate,
            engineerAvailability,
            engineerEmpId,
            password // Set the password field
        )

        // Generate a random document name
        val randomDocumentName = generateRandomDocumentName()

        // Save the engineer data to Firestore with the random document name
        engineerCollection
            .document(randomDocumentName)
            .set(engineer)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Engineer data inserted!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to insert engineer data.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun generateRandomDocumentName(): String {
        val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..20)
            .map { characters.random() }
            .joinToString("")
    }

    private fun generatePassword(name: String, joiningDate: String): String {
        // Get the first 4 letters of the engineer's name
        val namePrefix = name.take(4)


        // Extract the year from the joining date
        val year = joiningDate.substring(0, 4)

        // Combine the name prefix, 'a', and year to create the password
        return "${namePrefix.lowercase()}"+"$year"
    }
}