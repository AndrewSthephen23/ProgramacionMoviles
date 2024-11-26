package com.example.mykfirebaserehz.HuggingFace

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Multipart
interface HuellaApi {
    @Multipart
    @POST("/predict/")
    fun predict(@Part request: String): Call<ResponseData>
}