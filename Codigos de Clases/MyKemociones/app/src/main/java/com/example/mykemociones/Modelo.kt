package com.example.mykemociones

import okhttp3.MultipartBody

data class RequestData(val file: MultipartBody.Part)
data class ResponseData(val prediction: String)