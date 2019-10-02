package com.owenselles.addmenow

import com.google.firebase.Timestamp

data class User(
    val snapName: String? = null,
    val age: String? = null,
    val lastSeen: Timestamp? = null,
    val gender: String? = null
)

