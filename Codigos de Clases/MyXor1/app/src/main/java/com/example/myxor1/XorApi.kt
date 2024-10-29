package com.example.myxor1
import retrofit2.Call
import retrofit2.http.Body
//import retrofit2.http.GET
import retrofit2.http.POST

interface XorApi {
    @POST("/predict/")
    fun predict(@Body request: RequestData): Call<ResponseData>
}