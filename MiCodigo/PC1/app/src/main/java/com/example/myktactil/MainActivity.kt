package com.example.myktactil

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Display
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myktactil.databinding.ActivityMainBinding
import kotlin.io.path.Path
import android.graphics.*
import android.util.Log
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    // variable estatica
    val mBitmap = Bitmap.createBitmap(500,500,Bitmap.Config.ARGB_8888)
    val mCanvas = Canvas(mBitmap)
    val mPaint = Paint()
    companion object{
        var nodes = mutableListOf<Nodo>()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // todo lo que esta en el activity.xml lo pongo en la varible binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mitoolbar)

        // variable estatica
        // val mBitmap = Bitmap.createBitmap(500,500,Bitmap.Config.ARGB_8888)
        // val mCanvas = Canvas(mBitmap)
        // color del imageView
        mCanvas.drawColor(Color.GRAY)
        //Agrego configuracion a la imagen
        binding.myimg.setImageBitmap(mBitmap)

        // val mPaint = Paint()
        // color del objeto a dibujar
        mPaint.color = Color.BLACK
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 2F // el grosor del objeto a dibujar
        mPaint.isAntiAlias = true

        val alto = mCanvas.height.toFloat()
        val ancho = mCanvas.width.toFloat()
        // dibujar la linea horizontal X
        mCanvas.drawLine(0F,alto/2,ancho,alto/2,mPaint)
        // dibujar la linea vertical Y
        mCanvas.drawLine(ancho/2,0F,ancho/2,alto,mPaint)

        binding.myimg.setImageBitmap(mBitmap)

        //para saber la proporcion del tamañao del dispositivo
        val displayMetrics = DisplayMetrics().also {
            windowManager.defaultDisplay.getMetrics(it)
        }

        binding.myimg.setOnTouchListener( object : View.OnTouchListener{
            override fun onTouch(v: View, e: MotionEvent): Boolean {
                if (e.action == MotionEvent.ACTION_DOWN){
                    var proporcionancho = displayMetrics.widthPixels
                    var x = e.x*500/proporcionancho
                    var y = e.y*500/proporcionancho
                    val nombre = "nodo${nodes.size + 1}"
                    nodes.add(Nodo(nodes.size+1,x, y, nombre))

                    var mensaje1:String = "("+x.toString()+","+y.toString()+")"
                    binding.lblposicion.setText(mensaje1)
                    var mensaje2:String = "("+(x-250).toString()+","+(y-250).toString()+")"
                    binding.lblcordenada.setText(mensaje2)
                    // Actualiza la pantalla con el nuevo nodo
                    updateCanvas()
                    //mCanvas.drawCircle(x,y,2F,mPaint)
                    //binding.myimg.setImageBitmap(mBitmap)
                }
                return true
            }
        })

        binding.btngraficar.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                if (nodes.size >= 3){
                    // Redibuja la curva completa
                    drawBezierCurve()
                }else{
                    Toast.makeText(applicationContext,"Debe haber al menos 3 nodos",Toast.LENGTH_LONG).show()
                }
            }
        })

        val shouldUpdateCanvas = intent.getBooleanExtra("shouldUpdateCanvas", false)
        if (shouldUpdateCanvas) {
            updateCanvas() // actualizar el canvas
            drawBezierCurve() // Dibujar curva
        }
    }
    // Funcion para actualizar el canvas con los nodos actuales
    private fun updateCanvas(){
        mCanvas.drawColor(Color.GRAY)//Limpiamos el Canvas
        val alto = mCanvas.height.toFloat()
        val ancho = mCanvas.width.toFloat()
        // dibujar la linea horizontal X
        mCanvas.drawLine(0F,alto/2,ancho,alto/2,mPaint)
        // dibujar la linea vertical Y
        mCanvas.drawLine(ancho/2,0F,ancho/2,alto,mPaint)
        for (node in nodes){
            // Dibuja cada nodo
            mCanvas.drawCircle(node.x,node.y,2F,mPaint)
        }
        binding.myimg.setImageBitmap(mBitmap)
    }

    // Funcion para calcular un punto de la curva de Bezier usando el algoritmo de Casteljau
    private fun  calculateBezierPoint(t: Float, points: List<Nodo>): Nodo{
        var tempPoints = points.toMutableList()
        while (tempPoints.size > 1){
            val newPoints = mutableListOf<Nodo>()
            for (i in 0 until tempPoints.size - 1){
                val x = (1 - t)*tempPoints[i].x + t*tempPoints[i + 1].x
                val y = (1 - t)*tempPoints[i].y + t*tempPoints[i + 1].y
                newPoints.add(Nodo(i,x,y,""))
            }
            tempPoints = newPoints
        }
        return tempPoints[0]
    }

    // Funcion para graficar la curva de Bezier con todos los nodos
    private fun drawBezierCurve(){
        val path = Path()
        val resolution = 100 // Numero de puntos para calcular la curva

        // Mueve el path al primer nodo
        val startPoint = calculateBezierPoint(0f,nodes)
        path.moveTo(startPoint.x,startPoint.y)

        //Calcula los puntos de la curva y agregar al path
        for (i in 1..resolution){
            val t = i/resolution.toFloat()
            val point = calculateBezierPoint(t,nodes)
            //Dibujar un circulo en cada punto
            mCanvas.drawCircle(point.x,point.y,2F,mPaint)
            // Añadir el punto al path para la curva
            path.lineTo(point.x,point.y)
            // Debug: Imprimir los puntos generados en la curva
            Log.d("BezierPoint","Punto generado: (${point.x},${point.y})")
        }

        // Dibuja el path en el canvas
        mCanvas.drawPath(path,mPaint)
        binding.myimg.setImageBitmap(mBitmap)
        // Mostrar notificacion de exito
        val mensaje = "Curva de Bezier Graficada"
        Toast.makeText(applicationContext,mensaje,Toast.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mimenu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.mnulistar->{
                //Toast.makeText(applicationContext,"Listar",Toast.LENGTH_LONG).show()
                intent = Intent(applicationContext,MainListar::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}