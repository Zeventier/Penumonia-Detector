package com.example.pneumoniadetector.data.remote.response

import com.google.gson.annotations.SerializedName

data class ResultResponse(
    @field:SerializedName("error")
    val error: String,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listStory")
    val listStory: List<ResultItem>? = null
)

data class ResultItem(
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("pneumoniaType")
    val pneumoniaType: String,

    @field:SerializedName("accuracy")
    val accuracy: String,

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("photoUrl")
    val photoUrl: String
)

