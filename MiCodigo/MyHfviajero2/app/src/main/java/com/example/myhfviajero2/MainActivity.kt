package com.example.myhfviajero2

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myhfviajero2.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random
import android.graphics.*
import android.util.Log

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    // variable estatica
    var  ciudades: MutableList<Punto> = mutableListOf()
    lateinit var paint: Paint
    lateinit var canvas: Canvas
    lateinit var bitmap: Bitmap
    lateinit var paintText: Paint //Pintura para el texto
    lateinit var paintGraf: Paint
    //#
    lateinit var nodes: List<Nodo> // Lista de nodos para la curva de Bezier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Bitmap y Canvas para dibujar sobre el ImageView
        bitmap = Bitmap.createBitmap(500,500,Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
        canvas.drawColor(Color.GRAY)
        binding.imageView.setImageBitmap(bitmap)
        //Inicializacion de Paint para circulos
        paint = Paint()
        paint.color = Color.GREEN
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5F
        paint.isAntiAlias = true
        //Inicializacion de Paint para texto
        paintText = Paint()
        paintText.color = Color.BLACK
        paintText.textSize = 30F
        paintText.isAntiAlias = true
        //Paint para la grafica
        paintGraf = Paint()
        paintGraf.color = Color.BLUE
        paintGraf.style = Paint.Style.STROKE
        paintGraf.strokeWidth = 5F
        paintGraf.isAntiAlias = true

        // Para saber la proporcion del tamaño del dispositivo
        val displayMetrics = DisplayMetrics().also {
            windowManager.defaultDisplay.getMetrics(it)
        }
        // Capturar coordenadas de toques

        binding.imageView.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View, e: MotionEvent): Boolean {
                if (e.action == MotionEvent.ACTION_DOWN){
                    var proporcionancho = displayMetrics.widthPixels
                    val x = e.x*500/proporcionancho
                    val y = e.y*500/proporcionancho
                    ciudades.add(Punto(x.toInt(),y.toInt()))
                    val ciudadNumero = ciudades.size - 1
                    //Dibujar el punto en la pantalla
                    canvas.drawCircle(x.toFloat(),y.toFloat(),10f,paint)
                    canvas.drawText("$ciudadNumero",x.toFloat(),y.toFloat()-15,paintText)
                    binding.imageView.invalidate()

                    // Llama a updateCanvas para actualizar y dibujar la curva si hay al menos 4 puntos
                    if (ciudades.size >= 4) {
                        updateCanvas()
                    }
                }
                return true
            }
        })

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
        val miscoordenadas = Array(n){Punto(0,0)}
        for (i in 0..n-1){
            var x: Int = ciudades[i].x
            var y: Int = ciudades[i].y
            miscoordenadas[i] = Punto(x,y)
        }
        return miscoordenadas
    }
    fun calculateBezierPoint(t: Float, points: List<Nodo>): Nodo{
        var tempPoints = points.toMutableList()
        while (tempPoints.size > 1) {
            val newPoints = mutableListOf<Nodo>()
            for (i in 0 until tempPoints.size - 1) {
                val x = (1 - t) * tempPoints[i].x + t * tempPoints[i + 1].x
                val y = (1 - t) * tempPoints[i].y + t * tempPoints[i + 1].y
                newPoints.add(Nodo(i, x, y))
            }
            tempPoints = newPoints
        }
        return tempPoints[0]
    }
    fun drawBezierCurve() {
        val path = Path()
        val resolution = 100 // Número de puntos para calcular la curva

        val startPoint = calculateBezierPoint(0f, nodes)
        path.moveTo(startPoint.x, startPoint.y)

        for (i in 1..resolution) {
            val t = i / resolution.toFloat()
            val point = calculateBezierPoint(t, nodes)
            canvas.drawCircle(point.x, point.y, 2F, paintGraf)
            path.lineTo(point.x, point.y)
        }

        canvas.drawPath(path, paint)
        binding.imageView.invalidate() // Actualizar la vista
        Toast.makeText(applicationContext, "Curva de Bezier Graficada", Toast.LENGTH_LONG).show()
    }

    fun updateCanvas(){
        canvas.drawColor(Color.GRAY)//Limpiamos el Canvas
        for (ciudad in ciudades) {
            // Dibuja cada ciudad como un punto
            canvas.drawCircle(ciudad.x.toFloat(), ciudad.y.toFloat(), 10f, paint)
            // Dibuja el texto asociado a cada ciudad
            val ciudadNumero = ciudades.indexOf(ciudad) // O usa otro método para obtener el índice
            canvas.drawText("$ciudadNumero", ciudad.x.toFloat(), ciudad.y.toFloat() - 15, paintText)
        }

        var numciudades:Float = ciudades.size.toFloat()
        var tampoblacion:Float = binding.txttampoblacion.getText().toString().toFloat()
        var probabilidad_mutacion:Float = binding.txtprobabilidadMutacion.getText().toString().toFloat()
        var numgeneraciones:Float = binding.txtnumgeneraciones.getText().toString().toFloat()
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
                        var listaCiudades : MutableList<Int> = mutableListOf()
                        for(i in 0..numciudades.toInt()){
                            mensaje = mensaje +" - "+ it.prediction[i].toString()
                            if (i != numciudades.toInt()){
                                listaCiudades.add(it.prediction[i])
                            }
                        }
                        //#
                        nodes = listaCiudades.map { index -> Nodo(index, ciudades[index].x.toFloat(), ciudades[index].y.toFloat()) }

                        // Dibujar la curva de Bezier
                        drawBezierCurve()

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
}