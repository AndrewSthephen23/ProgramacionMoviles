package com.example.myktactil

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

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // todo lo que esta en el activity.xml lo pongo en la varible binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // variable estatica
        val mBitmap = Bitmap.createBitmap(500,500,Bitmap.Config.ARGB_8888)
        val mCanvas = Canvas(mBitmap)
        // color del imageView
        mCanvas.drawColor(Color.GRAY)
        //Agrego configuracion a la imagen
        binding.myimg.setImageBitmap(mBitmap)

        val mPaint = Paint()
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
                var proporcionancho = displayMetrics.widthPixels
                var x = e.x*500/proporcionancho
                var y = e.y*500/proporcionancho
                var mensaje1:String = "("+x.toString()+","+y.toString()+")"
                binding.lblposicion.setText(mensaje1)
                var mensaje2:String = "("+(x-250).toString()+","+(y-250).toString()+")"
                binding.lblcordenada.setText(mensaje2)
                mCanvas.drawCircle(x,y,2F,mPaint)
                binding.myimg.setImageBitmap(mBitmap)
                return true
            }
        })
        binding.btnP1.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                mPaint.color = Color.RED
                var x = 0
                var y = 0
                var mensaje1:String = "("+x.toString()+","+y.toString()+")"
                binding.lblposicion.setText(mensaje1)
                var mensaje2:String = "("+(x-250).toString()+","+(y-250).toString()+")"
                binding.lblcordenada.setText(mensaje2)
                mCanvas.drawCircle(x.toFloat(),y.toFloat(),2.9F,mPaint)
                binding.myimg.setImageBitmap(mBitmap)
            }
        })
        binding.btnP2.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                mPaint.color = Color.GREEN
                var x = 100
                var y = 0
                var mensaje1:String = "("+x.toString()+","+y.toString()+")"
                binding.lblposicion.setText(mensaje1)
                var mensaje2:String = "("+(x-250).toString()+","+(y-250).toString()+")"
                binding.lblcordenada.setText(mensaje2)
                mCanvas.drawCircle(x.toFloat(),y.toFloat(),2.9F,mPaint)
                binding.myimg.setImageBitmap(mBitmap)
            }
        })
        binding.btnP3.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                mPaint.color = Color.BLUE
                var x = 100
                var y = 100
                var mensaje1:String = "("+x.toString()+","+y.toString()+")"
                binding.lblposicion.setText(mensaje1)
                var mensaje2:String = "("+(x-250).toString()+","+(y-250).toString()+")"
                binding.lblcordenada.setText(mensaje2)
                mCanvas.drawCircle(x.toFloat(),y.toFloat(),2.9F,mPaint)
                binding.myimg.setImageBitmap(mBitmap)
                //Marcar el checkbox
                binding.chkfinalpresionado.isChecked = true
            }
        })
        binding.btngraficar.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                if (binding.chkfinalpresionado.isChecked){
                    mPaint.color = Color.BLACK
                    mPaint.style = Paint.Style.FILL
                    for (j in 0..100){
                        val t = (j/100.0).toFloat()
                        val x = 100 * (2 * t - t * t)
                        val y = 100 * t * t
                        // Graficar la curva
                        mCanvas.drawCircle(x,y,2.9F,mPaint)
                    }
                    binding.myimg.setImageBitmap(mBitmap)
                    //Mostrar notificacion
                    var mensaje:String = "Curva Graficada"
                    val myToast = Toast.makeText(applicationContext,mensaje,Toast.LENGTH_LONG)
                    myToast.show()
                }
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
}