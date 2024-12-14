package com.example.mykfirebasestoragerrostro.HuggingFace

// La manera de cómo se envía el dato
data class RequestData(val url: String)

// Esta es la manera como se recibe la respuesta
data class ResponseData(val prediction: String)
