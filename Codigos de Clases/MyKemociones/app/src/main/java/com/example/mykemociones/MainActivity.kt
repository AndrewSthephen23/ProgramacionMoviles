package com.example.mykemociones

import android.graphics.Bitmap
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.mykemociones.databinding.ActivityMainBinding
import android.provider.MediaStore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import android.content.Intent
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var mBitmap = Bitmap.createBitmap(512,512,Bitmap.Config.ARGB_8888)
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
                        val respuesta:String = response.body()?.prediction.toString()
                        binding.lblrespuesta.text = respuesta
                    } else {
                        Toast.makeText(this@MainActivity, "Failed to get prediction", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
        binding.btnenviar.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                Toast.makeText(applicationContext, "Iniciando proceso...",Toast.LENGTH_LONG).show()
                uploadImage(mBitmap)
            }
        })

        val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val selectedImage: Uri? = result.data?.data
                selectedImage?.let {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                    mBitmap = bitmap
                    binding.imgFoto.setImageBitmap(bitmap)
                }
            }
        }
        binding.btncargar.setOnClickListener {
            binding.lblrespuesta.setText("")
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(galleryIntent)
        }
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
    }
}