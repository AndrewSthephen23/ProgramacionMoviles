package com.example.mykexisterostro

import android.app.Activity
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mykexisterostro.databinding.ActivityMainBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    private val cameraPermissionRequestCode = 100
    lateinit var binding: ActivityMainBinding
    var mBitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fun uploadImage(bitmap: Bitmap) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), byteArray)
            val body = MultipartBody.Part.createFormData("file", "image.jpeg", requestBody)

            val call = RetrofitFoto.instance.predict(body)
            call.enqueue(object : Callback<ResponseData> {
                override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                    if (response.isSuccessful) {
                        val respuesta: String = response.body()?.prediction.toString()
                        binding.lblrespuesta.text = respuesta
                    } else {
                        Toast.makeText(this@MainActivity, "Predicción fallida", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        binding.btnenviar.setOnClickListener {
            Toast.makeText(applicationContext, "Iniciando proceso...", Toast.LENGTH_LONG).show()
            uploadImage(mBitmap)
        }

        // Reemplazar el comportamiento de btncargar para tomar una foto con la cámara
        binding.btncargar.setOnClickListener {
            handleCameraPermission()
        }
    }

    private fun startDefaultCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                takePictureLauncher.launch(takePictureIntent)
            } ?: run {
                Toast.makeText(this, "No hay aplicación de cámara disponible", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startDefaultCamera()
            }

            else -> {
                cameraPermissionRequestLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private val cameraPermissionRequestLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startDefaultCamera()
            } else {
                Toast.makeText(
                    this,
                    "Vaya a la configuración y habilite el permiso de la cámara para utilizar esta función",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private val takePictureLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val bitmap = result.data?.extras?.get("data") as Bitmap
                mBitmap = bitmap
                binding.imgFoto.setImageBitmap(bitmap)
                Toast.makeText(this, "Foto tomada", Toast.LENGTH_LONG).show()
            }
        }
}
