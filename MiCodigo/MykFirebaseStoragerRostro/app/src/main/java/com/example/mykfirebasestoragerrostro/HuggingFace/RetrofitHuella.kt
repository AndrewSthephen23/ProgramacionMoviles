package com.example.mykfirebasestoragerrostro.HuggingFace

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object RetrofitHuella {
    private const val BASE_URL = "https://andrew238-detectionface.hf.space"
    val getinstance: HuellaApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(HuellaApi::class.java)
    }
}