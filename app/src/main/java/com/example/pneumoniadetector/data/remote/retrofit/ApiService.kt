package com.example.pneumoniadetector.data.remote.retrofit

import com.example.pneumoniadetector.data.remote.response.ResultResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("list")
    suspend fun getResultList(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ResultResponse
}