package com.example.myxor1

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitXor {
    private const val BASE_URL = "https://raulhuarote-xor.hf.space/"
    val xorApi: XorApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(XorApi::class.java)
    }
}