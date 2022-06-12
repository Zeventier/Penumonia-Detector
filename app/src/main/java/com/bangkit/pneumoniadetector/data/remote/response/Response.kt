package com.bangkit.pneumoniadetector.data.remote.response

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
data class History(
    val name: String? = null,
    val prediction: String? = null,
    val accuracy: String? = null,
    val description: String? = null,
    val createdAt: String? = null,
    val photoUrl: String? = null,
    val userId: String? = null
){
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}

data class ResultResponse(
    @field:SerializedName("error")
    val error: String,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listStory")
    val listStory: List<ResultItem>? = null
)

@Parcelize
data class ResultItem(

    @field:SerializedName("id")
    var id: String?,

    @field:SerializedName("name")
    var name: String?,

    @field:SerializedName("pneumoniaType")
    var pneumoniaType: String?,

    @field:SerializedName("accuracy")
    var accuracy: String?,

    @field:SerializedName("createdAt")
    var createdAt: String?,

    @field:SerializedName("description")
    var description: String?,

    @field:SerializedName("photoUrl")
    var photoUrl: String?
): Parcelable
