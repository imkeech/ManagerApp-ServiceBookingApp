package com.example.serviceappmanager

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.widget.addTextChangedListener

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

import kotlin.random.Random

class addFragment : Fragment() {

    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var ph_no: EditText
    private lateinit var state: EditText
    private lateinit var city: EditText
    private lateinit var address: EditText
    private lateinit var pincode: EditText
    private lateinit var model: EditText
    private lateinit var modelId: EditText
    private lateinit var installationDate: EditText
    private lateinit var price: EditText
    private lateinit var amd: AutoCompleteTextView
    private lateinit var btnInsertData: Button
    private lateinit var firestore: FirebaseFirestore
    private lateinit var studentCollection: CollectionReference
    private lateinit var selectedinstallationDate: String

    private val amdOptions = arrayOf("Yes", "No")
    private var dataRetrieved = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        name = view.findViewById(R.id.name)
        email = view.findViewById(R.id.email)
        ph_no = view.findViewById(R.id.number)
        state = view.findViewById(R.id.state)
        city = view.findViewById(R.id.city)
        address = view.findViewById(R.id.address)
        pincode = view.findViewById(R.id.pincode)
        model = view.findViewById(R.id.model)
        modelId = view.findViewById(R.id.modelid)
        installationDate = view.findViewById(R.id.installationDate)
        price = view.findViewById(R.id.price)
        amd = view.findViewById(R.id.amd)
        btnInsertData = view.findViewById(R.id.add)

        // Initialize Firestore reference
        firestore = FirebaseFirestore.getInstance()
        studentCollection = firestore.collection("customerDetails")

        // Set up ArrayAdapter for AutoCompleteTextView
        val amdAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, amdOptions)
        amd.setAdapter(amdAdapter)

        installationDate.setOnClickListener {
            showDatePickerDialog()
        }





        // Add this code inside your onCreateView function after initializing the `ph_no` EditText
        ph_no.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val phoneNumber = s.toString()
                if (phoneNumber.length == 10) { // Assuming phone numbers are 10 digits long
                    checkPhoneNumberExists(phoneNumber)
                    if (dataRetrieved) {
                        setFieldsEditable(true)
                    }
                }
            }

        })



        btnInsertData.setOnClickListener {
            insertStudentData()
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
                selectedinstallationDate = formattedDate
                installationDate.setText(formattedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun insertStudentData() {
        val name = name.text.toString()
        val email = email.text.toString()
        val ph_no = "+91" + ph_no.text.toString()
        val state = state.text.toString()
        val city = city.text.toString()
        val address = address.text.toString()
        val pincode = pincode.text.toString()
        val model = model.text.toString()
        val modelid = modelId.text.toString()
        val AMDPeriod = price.text.toString()
        val no_of_coppies = amd.text.toString()

                if (name.isEmpty())
                {
                    Toast.makeText(requireContext(), "Please enter Name", Toast.LENGTH_SHORT).show()
                    return
                }
                if (!isValidEmail(email))
                {
                    Toast.makeText(requireContext(), "Please enter Correct Email", Toast.LENGTH_SHORT).show()
                    return
                }
                if (ph_no.isEmpty())
                {
                    Toast.makeText(requireContext(), "Please enter Phone Number", Toast.LENGTH_SHORT).show()
                    return
                }
                if (state.isEmpty())
                {
                    Toast.makeText(requireContext(), "Please enter State", Toast.LENGTH_SHORT).show()
                    return
                }
                if (city.isEmpty())
                {
                    Toast.makeText(requireContext(), "Please enter City", Toast.LENGTH_SHORT).show()
                    return
                }
                if (address.isEmpty())
                {
                    Toast.makeText(requireContext(), "Please enter Address", Toast.LENGTH_SHORT).show()
                    return
                }
                if (pincode.isEmpty())
                {
                    Toast.makeText(requireContext(), "Please enter Pincode", Toast.LENGTH_SHORT).show()
                    return
                }
                if (model.isEmpty())
                {
                    Toast.makeText(requireContext(), "Please enter Machine Model", Toast.LENGTH_SHORT).show()
                    return
                }
                if (modelid.isEmpty())
                {
                    Toast.makeText(requireContext(), "Please enter Machine Model Id", Toast.LENGTH_SHORT).show()
                    return
                }
                if (AMDPeriod.isEmpty())
                {
                    Toast.makeText(requireContext(), "Please enter peroid of AMD", Toast.LENGTH_SHORT).show()
                    return
                }

                // Validate AMD value
                if (no_of_coppies.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter no_of_coppies", Toast.LENGTH_SHORT).show()
                    return
                }

                val student = Students(name, email, ph_no, state, city, address, pincode, model, modelid, selectedinstallationDate, AMDPeriod, no_of_coppies)

        // Generate a random document name
        val randomDocumentName = generateRandomDocumentName()








        // Save the customer data to Firestore with the random document name
        studentCollection
            .document(randomDocumentName)
            .set(student)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Data inserted!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to insert data.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun generateRandomDocumentName(): String {
        val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..20)
            .map { characters.random() }
            .joinToString("")
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    // Add this function to check if the phone number exists in Firestore
    private fun checkPhoneNumberExists(phoneNumber: String) {



        studentCollection
            .whereEqualTo("ph_no", "+91$phoneNumber") // Assuming phone numbers are stored with the prefix "+91"
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Phone number exists in Firestore, populate the fields
                    val document = querySnapshot.documents[0] // Assuming there's only one matching document
                    name.setText(document.getString("name"))
                    email.setText(document.getString("email"))
                    ph_no.setText(document.getString("ph_no")?.substring(3) ?:"" ) // Remove "+91" prefix
                    state.setText(document.getString("state"))
                    city.setText(document.getString("city"))
                    address.setText(document.getString("address"))
                    pincode.setText(document.getString("pincode"))
                    ph_no.isFocusable = true // or simply remove this line if you want the default behavior

                    // You can populate other fields similarly
                    dataRetrieved = true

                    setFieldsEditable(true)
                }

            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to check phone number.", Toast.LENGTH_SHORT).show()
            }

    }

    private fun setFieldsEditable(editable: Boolean) {
        name.isEnabled = editable
        email.isEnabled = editable
        ph_no.isEnabled = editable
        state.isEnabled = editable
        city.isEnabled = editable
        address.isEnabled = editable
        pincode.isEnabled = editable
        model.isEnabled = editable
        modelId.isEnabled = editable
        installationDate.isEnabled = editable
        price.isEnabled = editable
        amd.isEnabled = editable
    }

}
