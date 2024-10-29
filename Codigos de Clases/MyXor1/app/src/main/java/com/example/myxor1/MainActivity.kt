package com.example.myxor1

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.view.Gravity
import com.example.myxor1.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnpredecir.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val x0 = if( binding.chkX0.isChecked) 1.0 else 0.0
                val x1 = if( binding.chkX1.isChecked) 1.0 else 0.0
                val requestData = RequestData(
                    listOf(
                        x0,
                        x1
                    )
                )
                val call = RetrofitXor.xorApi.predict(requestData)
                call.enqueue(object : Callback<ResponseData> {
                    override fun onResponse(
                        call: Call<ResponseData>,
                        response: Response<ResponseData>
                    ) {
                        if (response.isSuccessful) {
                            val responseData = response.body()
                            responseData?.let {
                                val prediction = it.prediction[0][0]
                                var mensaje = if (prediction == 1) "Positivo" else "Negativo"
                                binding.lblrespuesta.setText(mensaje)
                                val myToast = Toast.makeText(applicationContext,mensaje,Toast.LENGTH_SHORT)
                                myToast.setGravity(Gravity.LEFT,200,200)
                                myToast.show()
                            }
                        } else {
                            val myToast = Toast.makeText(applicationContext,"Error1",Toast.LENGTH_SHORT)
                            myToast.setGravity(Gravity.LEFT,200,200)
                            myToast.show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                        val myToast = Toast.makeText(applicationContext,"Error2",Toast.LENGTH_SHORT)
                        myToast.setGravity(Gravity.LEFT,200,200)
                        myToast.show()
                    }
                })
            }
        })
    }
}