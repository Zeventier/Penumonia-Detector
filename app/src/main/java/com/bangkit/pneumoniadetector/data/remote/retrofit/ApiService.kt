package com.bangkit.pneumoniadetector.data.remote.retrofit

import com.bangkit.pneumoniadetector.data.remote.response.PostPredictResponse
import com.bangkit.pneumoniadetector.data.remote.response.ResultResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @Multipart
    @POST("predict")
    fun postPredict(
        @Part file: MultipartBody.Part
    ): Call<PostPredictResponse>
}