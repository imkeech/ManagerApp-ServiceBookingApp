package com.example.serviceappmanager

// sample change -
// edit pante

data class User(
    val others: String? = null,
    val problems: String? = null,
    val modelId: String? = null,
    val name: String = "",
    val address: String = "",
    val ph_no: String = "", // Initialize with default value
    val amd: String = "", // Initialize with default value
    var isAccepted: Boolean? = null
)