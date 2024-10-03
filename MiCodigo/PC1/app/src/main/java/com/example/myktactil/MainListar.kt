package com.example.myktactil

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myktactil.databinding.ActivityMainBinding
import com.example.myktactil.databinding.ActivityMainListarBinding

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

        binding.lvlmisnodos.setOnItemClickListener{parent,view,position,id->
            val idnodo: Int = position
            val cordX: Float = MainActivity.nodes[position].x
            val cordY: Float = MainActivity.nodes[position].y
            val nombre: String = MainActivity.nodes[position].nombre

            intent = Intent(applicationContext,MainAgregar::class.java)
            with(intent){
                putExtra("idnodo",idnodo)
                putExtra("coordenadaX",cordX)
                putExtra("coordenadaY",cordY)
                putExtra("nombre",nombre)
            }
            startActivity(intent)
        }
    }
}