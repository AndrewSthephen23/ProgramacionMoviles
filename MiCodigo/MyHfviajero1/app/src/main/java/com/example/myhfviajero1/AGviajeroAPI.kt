package com.example.myhfviajero1

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface AGviajeroAPI {
    @POST("/predict/")
    fun predict(@Body request: RequestData): Call<ResponseData>
}