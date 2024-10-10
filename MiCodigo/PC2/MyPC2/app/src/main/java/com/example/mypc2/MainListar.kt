package com.example.mypc2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.example.mypc2.databinding.ActivityMainListarBinding

class MainListar : AppCompatActivity() {
    lateinit var binding: ActivityMainListarBinding
    var adapter: MyAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_listar)
        binding = ActivityMainListarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = MyAdapter(applicationContext)
        binding.lvlmisnodos.adapter = adapter

        binding.lvlmisnodos.setOnItemLongClickListener{parent,view,position,id->
            val nodo = MainActivity.nodes[position]
            AlertDialog.Builder(this)
                .setTitle("Eliminar nodo")
                .setMessage("¿Estás seguro de que quieres eliminar el nodo '${nodo.nombre}'?")
                .setPositiveButton("Sí") { dialog, which ->
                    // Eliminar el nodo
                    MainActivity.nodes.removeAt(position)

                    //Actualizar la lista de ciudades en MainActivity
                    if (position < MainActivity.ciudades.size) {
                        MainActivity.ciudades.removeAt(position) // Eliminar la ciudad correspondiente
                    }

                    // Notificar al adaptador que los datos han cambiado
                    adapter?.notifyDataSetChanged()
                    // Regresar a MainActivity y actualizar el canvas
                    val intent = Intent(this@MainListar, MainActivity::class.java)
                    intent.putExtra("shouldUpdateCanvas", true) // Indica que debe actualizar el canvas
                    startActivity(intent)
                    finish() // Finaliza la actividad actual
                }
                .setNegativeButton("No", null)
                .show()
            true // Indica que el evento ha sido manejado

        }
    }
}