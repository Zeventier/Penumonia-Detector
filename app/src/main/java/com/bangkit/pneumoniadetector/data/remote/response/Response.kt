package com.bangkit.pneumoniadetector.data.remote.response

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class History(
    val name: String? = null,
    val prediction: String? = null,
    val accuracy: String? = null,
    val description: String? = null,
    val photoUrl: String? = null,
    val userId: String? = null
){
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}