package com.example.serviceappmanager

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class CloudMessaging {
    private val token = "AAAAAKTurd4:APA91bFvbhYtzDcebnqlbb_poBJ5UgQyqplDpw9myQVNJ72hjXWc31_NqB0qoK0a0ktJbjrPEaPwtohZZfdVk-WBlbAt85mDUXHoTCm0--9fG3Wx0yCUfGGaqmhFcTZmdSyoFQdctuOj"
    private val httpClient = okhttp3.OkHttpClient()
    private val notifDbRef = Firebase.firestore.collection("notifications")

    fun sendEngineerNotification(engineerId: String, phoneNumber: String) {
        val data = mapOf(
            "destination" to "engineer",
            "toEngineerId" to engineerId,
            "description" to "You have been alotted a service for customer: $phoneNumber"
        )
        sendMessage(data)
    }

    fun sendCloudMessage(phoneNumber: String, message: String) {
        val data = mapOf("phone" to phoneNumber, "description" to message)
        sendMessage(data)
    }

    private fun sendMessage(data: Map<String, String>) {
        val payloadObj = mapOf("to" to "/topics/booking", "data" to data)
        val payload = JSONObject(payloadObj).toString()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val json = payload.toRequestBody(mediaType)

        val req = Request.Builder()
            .url("https://fcm.googleapis.com/fcm/send")
            .addHeader("Authorization", "key=$token")
            .post(json)
            .build()

        httpClient.newCall(req).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                Log.d("fcm", "successfully posted")

                // Add the data to the collection after the Cloud Message is sent successfully
                notifDbRef.add(data)
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.d("fcm", "failed to send event")
            }
        })
    }
}