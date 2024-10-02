package com.example.myalgoritmogenetico

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnaccion.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                var numciudades: Int = binding.txtnumciudades.getText().toString().toInt()
                var tampoblacion: Int = binding.txttampoblacion.getText().toString().toInt()
                var probabilidad_mutacion: Double = binding.txtprobabilidadMutacion.getText().toString().toDouble()
                var numgeneraciones: Int = binding.txtnumgeneraciones.getText().toString().toInt()

                /*
                var I1 : Individuo = Individuo(numciudades)
                var mensaje: String = I1.get_camino() + "==>" + I1.get_distancia(generar_coordenada(numciudades)).toString()
                val myToast = Toast.makeText(applicationContext,mensaje,Toast.LENGTH_LONG)
                myToast.show()
                 */
                /*
                var poblacion: Array<Individuo> = Array(tampoblacion){Individuo(numciudades)}
                var coordenadas: Array<Punto> = generar_coordenada(numciudades)
                var mensaje: String = poblacion[0].get_camino() + "==>" + poblacion[0].get_distancia(coordenadas).toString()
                poblacion = calcular_aptitud(poblacion,coordenadas)
                mensaje = mensaje + "nuevo: " +  poblacion[0].get_distancia(coordenadas).toString()
                val myToast = Toast.makeText(applicationContext,mensaje,Toast.LENGTH_LONG)
                myToast.show()
                */
                var poblacion: Array<Individuo> = Array(tampoblacion){Individuo(numciudades)}
                var coordenadas: Array<Punto> = generar_coordenada(numciudades)
                var primeradistancia:Int = poblacion[0].get_distancia(coordenadas)
                poblacion = calcular_aptitud(poblacion,coordenadas)
                for (t in 0 .. numgeneraciones-1){
                    var seleccionados: Array<Individuo> = seleccion_torneo(poblacion)
                    for (i in 0 .. seleccionados.size-1 step 2){
                        var padre1: Individuo = seleccionados[i]
                        var padre2: Individuo = seleccionados[i+1]
                        var hijo1: Individuo = cruzar(padre1,padre2)
                        var hijo2: Individuo = cruzar(padre2,padre1)
                        hijo1 = mutar(hijo1,probabilidad_mutacion)
                        hijo2 = mutar(hijo2,probabilidad_mutacion)
                        poblacion[i] = hijo1
                        poblacion[i+1] = hijo2
                    }
                    poblacion = calcular_aptitud(poblacion,coordenadas)
                }
                var mensaje: String = poblacion[0].get_camino() + "==>" + poblacion[0].get_distancia(coordenadas).toString()
                binding.lblresultado.setText(primeradistancia.toString() + " " + mensaje)
                val myToast = Toast.makeText(applicationContext,mensaje,Toast.LENGTH_LONG)
                myToast.show()
            }
        })
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
         */
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