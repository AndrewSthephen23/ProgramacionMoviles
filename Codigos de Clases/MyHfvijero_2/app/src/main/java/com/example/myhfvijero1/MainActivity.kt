package com.example.myhfvijero1

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.myhfvijero1.databinding.ActivityMainBinding
import retrofit2.Callback
import retrofit2.Call
import  retrofit2.Response
import android.widget.Toast
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnaccion.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                var numciudades:Float = binding.txtnumciudades.getText().toString().toFloat()
                var tampoblacion:Float = binding.txttampoblacion.getText().toString().toFloat()
                var probabilidad_mutacion:Float = binding.txtprobabilidadMutacion.getText().toString().toFloat()
                var numgeneraciones:Float = binding.txtnumgereraciones.getText().toString().toFloat()
                var coordenadas:Array<Punto> = generar_coordenadas(numciudades.toInt())

                val requestData = RequestData(
                    listOf(numciudades,tampoblacion,probabilidad_mutacion,numgeneraciones),
                    arreglo_puntos(coordenadas)
                )
                val call = RetrofitAGviajero.aGviajeroAPI.predict(requestData)
                    call.enqueue(object : Callback<ResponseData> {
                        override fun onResponse(
                            call: Call<ResponseData>,
                            response: Response<ResponseData>
                        ) {
                            if(response.isSuccessful){
                                val responseData = response.body()
                                responseData?.let{
                                    var mensaje: String = ""
                                    for(i in 0..numciudades.toInt()){
                                        mensaje = mensaje +" - "+ it.prediction[i].toString()
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
                            val myToast = Toast.makeText(applicationContext,"Error2:"+mensaje,Toast.LENGTH_LONG)
                            myToast.show()
                        }
                    })
            }
        })
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
    }
    fun arreglo_puntos(Pts:Array<Punto>):IntArray{
        val salida = IntArray(2*Pts.size){it}
        var contador:Int = 0
        for(i in 0..Pts.size-1){
            salida [contador] = Pts[i].x
            salida [contador+1] = Pts[i].y
            contador = contador + 2
        }
        return salida
    }
    fun generar_coordenadas(n:Int):Array<Punto>{
        val random = Random(47)
        val miscoordenadas = Array(n){Punto(0,0)}
        for (i in 0..n-1){
            var x: Int = 5 + ThreadLocalRandom.current().nextInt(100)
            var y: Int = 5 + ThreadLocalRandom.current().nextInt(100)
            miscoordenadas[i] = Punto(x,y)
        }
        return miscoordenadas
    }
}