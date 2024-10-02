package com.example.myhfvijero1
// Los datos como se van a enviar
data class RequestData(val data:List<Float>)
// los datos como se van a recibir
data class ResponseData(val prediction:List<Int>)
