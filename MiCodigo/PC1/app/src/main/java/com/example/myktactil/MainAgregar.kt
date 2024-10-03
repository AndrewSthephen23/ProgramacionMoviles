package com.example.myktactil

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myktactil.databinding.ActivityMainAgregarBinding
import com.example.myktactil.databinding.ActivityMainListarBinding

class MainAgregar : AppCompatActivity() {
    lateinit var binding: ActivityMainAgregarBinding
    var idnodo: Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_agregar)
        binding = ActivityMainAgregarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle:Bundle?= intent.extras
        bundle?.let {
            bundle.apply {
                val cordX:Float? = getFloat("coordenadaX")
                val cordY:Float? = getFloat("coordenadaY")
                val nombre:String? = getString("nombre")

                idnodo = getInt("idnodo")
                binding.txtcorX.setText(cordX.toString())
                binding.txtcorY.setText(cordY.toString())
                binding.txtnombre.setText(nombre)
                binding.btnagregar.setText("Modificar")

                // Vamos a mostrar el boton eliminar cuando se va a modificar un Nodo
                binding.btneliminar.visibility = View.VISIBLE
            }
        }

        binding.btnagregar.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                var cordX: String = binding.txtcorX.text.toString()
                var cordY: String = binding.txtcorY.text.toString()
                var nombre: String = binding.txtnombre.text.toString()
                var c = Nodo(idnodo,cordX.toFloat(),cordY.toFloat(),nombre)
                if (binding.btnagregar.text == "Agregar"){
                    MainActivity.nodes.add(c)
                    Toast.makeText(applicationContext,"Agregado",Toast.LENGTH_LONG).show()
                }else{
                    MainActivity.nodes.set(c.idnodo,c)
                    Toast.makeText(applicationContext,"Modificado",Toast.LENGTH_LONG).show()
                }
                binding.txtcorX.setText("")
                binding.txtcorY.setText("")
                binding.txtnombre.setText("")
                //Hacemos invisible a boton eliminar cuando se esta agregando contactos
                binding.btneliminar.visibility = View.GONE

            }
        })

        binding.btnlistar.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                intent = Intent(applicationContext,MainListar::class.java)
                startActivity(intent)
            }
        })

        binding.btneliminar.setOnClickListener(object  : View.OnClickListener{
            override fun onClick(v: View?) {
                if (idnodo in MainActivity.nodes.indices){
                    MainActivity.nodes.removeAt(idnodo)
                    Toast.makeText(applicationContext,"Nodo Eliminado",Toast.LENGTH_LONG).show()
                    binding.txtcorX.setText("")
                    binding.txtcorY.setText("")
                    binding.txtnombre.setText("")
                    //Hacemos invisible a boton eliminar cuando se esta agregando contactos
                    binding.btneliminar.visibility = View.GONE
                    binding.btngraficar2.visibility = View.VISIBLE
                }
            }
        })

        binding.btngraficar2.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                // Crear un Intent para ir a MainActivity
                val intent = Intent(applicationContext, MainActivity::class.java)
                // Pasamos un extra para indicar que se debe ejecutar updateCanvas
                intent.putExtra("shouldUpdateCanvas", true)
                startActivity(intent)
            }
        })

    }
}