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

class MainAgregar : AppCompatActivity() {
    lateinit var binding: ActivityMainAgregarBinding
    var idcontacto: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_agregar)
        binding = ActivityMainAgregarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle: Bundle? = intent.extras

        bundle?.let {
            val nombre: String? = it.getString("nombre")
            val alias: String? = it.getString("alias")
            val codigo: String? = it.getString("codigo")
            idcontacto = it.getInt("idcontacto")

            binding.txtnombre.setText(nombre)
            binding.txtalias.setText(alias)
            binding.txtcodigo.setText(codigo)
            binding.btnagregar.text = "Modificar"

            // Muestra el botón de eliminar cuando se está editando un contacto
            binding.btneliminar.visibility = View.VISIBLE
        }

        // Acción del botón Agregar/Modificar
        binding.btnagregar.setOnClickListener {
            val nombre = binding.txtnombre.text.toString()
            val alias = binding.txtalias.text.toString()
            val codigo = binding.txtcodigo.text.toString()
            val contacto = Contacto(idcontacto, nombre, alias, codigo)

            if (binding.btnagregar.text == "Agregar") {
                Global.miscontactos.add(contacto)
                Toast.makeText(applicationContext, "Agregado", Toast.LENGTH_LONG).show()
            } else {
                Global.miscontactos[idcontacto] = contacto
                Toast.makeText(applicationContext, "Modificado", Toast.LENGTH_LONG).show()
            }
            limpiarCampos()
        }

        // Acción del botón Listar
        binding.btnlistar.setOnClickListener {
            intent = Intent(applicationContext, MainListar::class.java)
            startActivity(intent)
        }

        // Acción del botón Eliminar
        binding.btneliminar.setOnClickListener {
            if (idcontacto >= 0 && idcontacto < Global.miscontactos.size) {
                Global.miscontactos.removeAt(idcontacto)
                Toast.makeText(applicationContext, "Contacto eliminado", Toast.LENGTH_LONG).show()
                limpiarCampos()
                finish()  // Vuelve a la pantalla anterior
            }
        }
    }

    private fun limpiarCampos() {
        binding.txtnombre.setText("")
        binding.txtalias.setText("")
        binding.txtcodigo.setText("")
        binding.btnagregar.text = "Agregar"
        binding.btneliminar.visibility = View.GONE // Ocultar el botón eliminar nuevamente
    }
}