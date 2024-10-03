package com.example.myhfviajero1

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myhfviajero1.databinding.ActivityMainBinding
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnaccion.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                var numciudades: Float = binding.txtnumciudades.getText().toString().toFloat()
                var tampoblacion: Float = binding.txttampoblacion.getText().toString().toFloat()
                var probabilidad_mutacion: Float = binding.txtprobabilidadMutacion.getText().toString().toFloat()
                var numgeneraciones: Float = binding.txtnumgeneraciones.getText().toString().toFloat()

                val requestData = RequestData(
                    listOf(numciudades,tampoblacion,probabilidad_mutacion,numgeneraciones)
                )
                val call = RetrofitAGviajero.aGviajeroAPI.predict(requestData)
                call.enqueue(object : Callback<ResponseData> {
                    override fun onResponse(
                        call: Call<ResponseData>,
                        response: Response<ResponseData>
                    ) {
                        if (response.isSuccessful){
                            val responseData = response.body()
                            responseData?.let {
                                var mensaje: String = ""
                                for (i in 0 .. numciudades.toInt()-1){
                                    mensaje = mensaje + " - " + it.prediction[i].toString()
                                }
                                mensaje = "La mejor ruta es: " + mensaje
                                binding.lblresultado.setText(mensaje)
                            }
                        }else{
                            val myToast = Toast.makeText(applicationContext,"Error1",Toast.LENGTH_LONG)
                            myToast.show()
                        }
                    }
                    override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                        var mensaje: String = t.message.toString()
                        val myToast = Toast.makeText(applicationContext,"Error2"+mensaje,Toast.LENGTH_LONG)
                        myToast.show()
                    }
                })
            }
        })

    }
}