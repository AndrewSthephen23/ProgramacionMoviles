package com.example.myalgoritmogenetico

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myalgoritmogenetico.databinding.ActivityMainBinding
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    // Modificacion1
    var  ciudades: MutableList<Punto> = mutableListOf()
    lateinit var paint: Paint
    lateinit var canvas: Canvas
    lateinit var bitmap: Bitmap
    lateinit var paintText: Paint //Pintura para el texto
    //M1

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
                    val ciudadNumero = ciudades.size
                    //Dibujar el punto en la pantalla
                    canvas.drawCircle(x.toFloat(),y.toFloat(),10f,paint)
                    canvas.drawText("$ciudadNumero",x.toFloat(),y.toFloat()-15,paintText)
                    binding.imageView.invalidate()
                }
                return true
            }
        })

        binding.btnaccion.setOnClickListener{
            if (ciudades.size > 1){
                ejecutarAlgoritmoGenetico()
            }else{
                Toast.makeText(this,"Por favor seleccione al menos 2 ciudades",Toast.LENGTH_LONG).show()
            }
        }
    }
    fun ejecutarAlgoritmoGenetico(){
        val numciudades = ciudades.size
        val tampoblacion = binding.txttampoblacion.getText().toString().toInt()
        val probabilidad_mutacion = binding.txtprobabilidadMutacion.getText().toString().toDouble()
        val numgeneraciones = binding.txtnumgeneraciones.getText().toString().toInt()

        var poblacion: Array<Individuo> = Array(tampoblacion){Individuo(numciudades)}
        var coordenadas = ciudades.toTypedArray()
        var primeradistancia:Int = poblacion[0].get_distancia(coordenadas)
        poblacion = calcular_aptitud(poblacion,coordenadas)

        for (t in 0 until  numgeneraciones){
            val seleccionados = seleccion_torneo(poblacion)
            for (i in seleccionados.indices step 2){
                val padre1 = seleccionados[i]
                val padre2 = seleccionados[i+1]
                val hijo1 = mutar(cruzar(padre1,padre2),probabilidad_mutacion)
                val hijo2 = mutar(cruzar(padre2,padre1),probabilidad_mutacion)
                poblacion[i] = hijo1
                poblacion[i+1] = hijo2
            }
            poblacion = calcular_aptitud(poblacion,coordenadas)
        }

        val mejorIndividuo = poblacion[0]
        val camino = mejorIndividuo.get_camino()
        // Usamos una expresión regular para encontrar números en la cadena
        val regex = "-?\\d+".toRegex()
        val arregloCamino = regex.findAll(camino).map { it.value.toInt() } // Convertimos cada número encontrado a Int
        val ArrayCamino = arregloCamino.toMutableList()
        //Dibujar la ruta en el ImageView
        println("Cromosoma: ${mejorIndividuo.cromosoma.joinToString(", ")}")
        println("ArrayCamino: ${ArrayCamino.joinToString (",")}")
        dibujarRuta(ArrayCamino)

        val mensaje = "Ruta:($camino) ==> ${mejorIndividuo.get_distancia(coordenadas)}"
        binding.lblresultado.text = "${primeradistancia} $mensaje"
        val myToast = Toast.makeText(applicationContext,mensaje,Toast.LENGTH_LONG)
        myToast.show()


    }

    fun dibujarRuta(arrayCamino: MutableList<Int>){
        // Limpiar solo la ruta, mantener las ciudades
        canvas.drawColor(Color.GRAY)//Redibujar fonfo gris
        for (i in ciudades.indices){
            val ciudad = ciudades[i]
            canvas.drawCircle(ciudad.x.toFloat(),ciudad.y.toFloat(),10f,paint)
            canvas.drawText("${i+1}",ciudad.x.toFloat(),ciudad.y.toFloat()-15,paintText)
        }
        paint.color = Color.BLACK

        for (i in 0 until  arrayCamino.size-1){
            val indiceCiudad1 = arrayCamino[i] // Obtener el índice de la ciudad del cromosoma
            val indiceCiudad2 = arrayCamino[i + 1] // Siguiente ciudad en la ruta

            val p1 = ciudades[indiceCiudad1] // Obtener las coordenadas de la primera ciudad
            val p2 = ciudades[indiceCiudad2] // Obtener las coordenadas de la siguiente ciudad
            canvas.drawLine(p1.x.toFloat(),p1.y.toFloat(),p2.x.toFloat(),p2.y.toFloat(),paint)
        }

        binding.imageView.invalidate()
    }

    fun mutar(Hijo:Individuo,Pm:Double):Individuo{
        var aleatorio:Double = ThreadLocalRandom.current().nextDouble()
        if (aleatorio<Pm){
            var indice1:Int = ThreadLocalRandom.current().nextInt(Hijo.cromosoma.size)
            var indice2:Int = indice1
            while (indice1==indice2){
                indice2 = ThreadLocalRandom.current().nextInt(Hijo.cromosoma.size)
            }
            var t = Hijo.cromosoma[indice1]
            Hijo.cromosoma[indice1] = Hijo.cromosoma[indice2]
            Hijo.cromosoma[indice2] = t

        }
        return Hijo
    }
    fun cruzar(Padre1:Individuo,Padre2:Individuo):Individuo{
        var punto_cruce:Int = ThreadLocalRandom.current().nextInt(Padre1.cromosoma.size-1)
        var Hijo:Individuo = Individuo(Padre1.num_ciudades)
        for (i in 0 .. punto_cruce-1){
            Hijo.cromosoma[i] = Padre1.cromosoma[i]
        }
        for (i in punto_cruce .. Padre1.num_ciudades-1){
            Hijo.cromosoma[i] = Padre2.cromosoma[i]
        }
        return Hijo
    }
    fun seleccion_torneo(pobla: Array<Individuo>):Array<Individuo>{
        var seleccionados: Array<Individuo> = Array(pobla.size){Individuo(pobla[0].cromosoma.size)}
        for (i in 0 .. pobla.size-1){
            var indice1:Int = ThreadLocalRandom.current().nextInt(pobla.size)
            var indice2:Int = indice1
            while (indice1 == indice2){
                indice2 = ThreadLocalRandom.current().nextInt(pobla.size)
            }
            var competidor1:Individuo = pobla[indice1]
            var competidor2:Individuo = pobla[indice2]
            if (competidor1.distancia < competidor2.distancia){
                seleccionados[i] = competidor1
            }else{
                seleccionados[i] = competidor2
            }
        }
        return seleccionados
    }
    fun calcular_aptitud(pobla:Array<Individuo>, C:Array<Punto>):Array<Individuo>{
        var ordenado: Array<Individuo> = Array(pobla.size){Individuo(pobla[0].cromosoma.size)}
        var aptitud: IntArray = IntArray(pobla.size){0}
        for (i in 0 .. pobla.size-1){
            aptitud[i] = pobla[i].get_distancia(C)
        }
        aptitud.sort()// ordena de menor a mayor
        for (i in 0 .. pobla.size-1){
            for (j in 0 .. pobla.size-1){
                if (aptitud[i] == pobla[j].distancia){
                    ordenado[i] = pobla[j]
                    break
                }
            }
        }
        return ordenado
    }
    fun generar_coordenada(n:Int):Array<Punto>{
        var random = Random(47)
        var miscoordenadas = Array(n){Punto(0,0)}
        for (i in 0 .. n-1){
            var x:Int = 5 + ThreadLocalRandom.current().nextInt(100)
            var y:Int = 5 + ThreadLocalRandom.current().nextInt(100)
            miscoordenadas[i] = Punto(x,y)
        }
        return miscoordenadas
    }
}