package com.example.serviceappmanager

// summa intha mari change panra vechiko
// inga side la nee ena file change pana nu varu athu elame select pantu
// description type pantu commit and push nu podu

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