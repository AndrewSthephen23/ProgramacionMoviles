package com.example.mykcontactos

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mykcontactos.databinding.ActivityMainListarBinding

class MainListar : AppCompatActivity() {
    lateinit var binding: ActivityMainListarBinding
    var adapter : MyAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_listar)
        binding = ActivityMainListarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = MyAdapter(applicationContext)
        binding.lvmiscontactos.adapter = adapter
        binding.lvmiscontactos.setOnItemClickListener{parent,view,position,id->
            val nombre:String = Global.miscontactos[position].nombre
            val alias:String = Global.miscontactos[position].alias
            val codigo:String = Global.miscontactos[position].codigo
            val idcontacto:Int = position

            //val mensaje:String = nombre+ " "+alias
            //Toast.makeText(applicationContext,mensaje,Toast.LENGTH_LONG).show()
            intent = Intent(applicationContext,MainAgregar::class.java)
            with(intent){
                putExtra("nombre",nombre)
                putExtra("alias",alias)
                putExtra("codigo",codigo)
                putExtra("idcontacto",idcontacto)
            }
            startActivity(intent)
        }

        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
    }
}