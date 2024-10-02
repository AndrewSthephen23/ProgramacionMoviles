package com.example.mykcontactos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mykcontactos.databinding.ActivityMainAgregarBinding
import com.example.mykcontactos.databinding.ActivityMainBinding

class MainAgregar : AppCompatActivity() {
    lateinit var binding: ActivityMainAgregarBinding
    var idcontacto: Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_agregar)
        binding = ActivityMainAgregarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle:Bundle? = intent.extras
        bundle?.let {
            bundle.apply {
                val nombre:String? = getString("nombre")
                val alias:String? = getString("alias")
                val codigo:String? = getString("codigo")
                idcontacto = getInt("idcontacto")
                binding.txtnombre.setText(nombre)
                binding.txtalias.setText(alias)
                binding.txtcodigo.setText(codigo)
                binding.btnagregar.setText("Modificar")

                // Vamos a mostrar el boton eliminar cuando se va modificar un Contacto
                binding.btneliminar.visibility = View.VISIBLE
            }
        }

        binding.btnagregar.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                //TODO("Not yet implemented")
                var nombre: String = binding.txtnombre.text.toString()
                var alias: String = binding.txtalias.text.toString()
                var codigo: String = binding.txtcodigo.text.toString()
                var c = Contacto(idcontacto,nombre,alias,codigo)
                if (binding.btnagregar.text == "Agregar"){
                    Global.miscontactos.add(c)
                    Toast.makeText(applicationContext,"Agregado",Toast.LENGTH_LONG).show()
                }else{
                    Global.miscontactos. set(c.idcontacto,c)
                    Toast.makeText(applicationContext,"Modificado",Toast.LENGTH_LONG).show()
                }
                binding.txtnombre.setText("")
                binding.txtalias.setText("")
                binding.txtcodigo.setText("")
                // Hacemos invisible a boton eliminar cuando se esta agregando contactos
                binding.btneliminar.visibility = View.GONE
            }
        })

        binding.btnlistar.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                intent = Intent(applicationContext,MainListar::class.java)
                startActivity(intent)
                /*
                for (c in Global.miscontactos){
                    var mensaje: String = (c.idcontacto.toString())+" "+c.nombre+" "+c.alias
                    Toast.makeText(applicationContext,mensaje,Toast.LENGTH_LONG).show()
                }
                 */
            }
        })

        binding.btneliminar.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                if (idcontacto in Global.miscontactos.indices){
                    Global.miscontactos.removeAt(idcontacto)
                    Toast.makeText(applicationContext,"Contacto Eliminado",Toast.LENGTH_LONG).show()
                    binding.txtnombre.setText("")
                    binding.txtalias.setText("")
                    binding.txtcodigo.setText("")
                    binding.btnagregar.text = "Agregar"
                    // Hacemos invisible a boton eliminar cuando se elimino el contacto
                    binding.btneliminar.visibility = View.GONE
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