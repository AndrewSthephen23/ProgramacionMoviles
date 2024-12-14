package com.example.mykfirebasestoragerrostro.HuggingFace

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface HuellaApi {
    @POST("/predict/")
    fun predict(@Body request: RequestData): Call<ResponseData>
}
