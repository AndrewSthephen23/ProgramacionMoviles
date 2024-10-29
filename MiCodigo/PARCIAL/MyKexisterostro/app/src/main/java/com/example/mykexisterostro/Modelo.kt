package com.example.mykexisterostro

import okhttp3.MultipartBody

// La manera de como se envia el dato
data class RequestData(val file: MultipartBody.Part)

data class ResponseData(val prediction: String)