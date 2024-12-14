package com.example.mykfirebasestoragerrostro

import androidx.activity.result.ActivityResultLauncher
import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.mykfirebasestoragerrostro.databinding.ActivityMainBinding
import com.google.firebase.database.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.util.Log
import android.widget.AdapterView.OnItemLongClickListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.mykfirebasestoragerrostro.HuggingFace.RequestData
import com.example.mykfirebasestoragerrostro.HuggingFace.ResponseData
import com.example.mykfirebasestoragerrostro.HuggingFace.RetrofitHuella
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileOutputStream
import javax.annotation.Nonnull

class MainActivity : AppCompatActivity() {
    private var misFotos = ArrayList<Foto>()
    lateinit var binding: ActivityMainBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase

    val storageReference: StorageReference = FirebaseStorage.getInstance().getReference()

    var mBitmap = Bitmap.createBitmap(512,512,Bitmap.Config.ARGB_8888)
    private var Miuri: Uri? = null
    var adapter: MyAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnenviar.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                if (Miuri != null) {
                    val reference = storageReference.child("imgrostro/" + UUID.randomUUID().toString())

                    reference.putFile(Miuri!!).addOnSuccessListener {
                        reference.downloadUrl.addOnSuccessListener { uri: Uri ->

                            val miURL = uri.toString()
                            val requestData = RequestData(miURL)
                            val nombre = binding.txtnombre.getText().toString()
                            //val key = databaseReference.child("Rostro").push().getKey()

                            // Llamar al servicio HuggingFace para predecir
                            val call = RetrofitHuella.getinstance.predict(requestData)
                            call.enqueue(object : Callback<ResponseData> {
                                override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                                    if (response.isSuccessful) {
                                        val hayrostro = response.body()?.prediction ?: "NO"
                                        val key = databaseReference.child("Fotos").push().key ?: ""
                                        val foto = Foto(nombre, key, miURL, hayrostro)

                                        // Guardar la clase Foto en Firebase Realtime Database
                                        databaseReference.child("Fotos").child(key).setValue(foto)

                                        Toast.makeText(applicationContext, "Foto guardada: $hayrostro", Toast.LENGTH_LONG).show()
                                        binding.txtnombre.text.clear()
                                    } else {
                                        Toast.makeText(applicationContext, "Error en la predicción", Toast.LENGTH_LONG).show()
                                    }
                                }

                                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                                    Toast.makeText(applicationContext, "Error al contactar el servicio", Toast.LENGTH_LONG).show()
                                }
                            })
                        }.addOnFailureListener { exception ->
                            Toast.makeText(applicationContext, "Error al obtener URL: ${exception.message}", Toast.LENGTH_LONG).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(applicationContext, "Fallo al subir la imagen", Toast.LENGTH_LONG).show()
                    }
                }
            }

        })



        val escucha = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                misFotos.clear()
                for (postSnapshot in dataSnapshot.children) {
                    for (postSnapshot1 in postSnapshot.children){
                        var nombre:String = postSnapshot1.child("nombre").value.toString()
                        var key:String = postSnapshot1.child("key").value.toString()
                        var urlFoto:String = postSnapshot1.child("urlFoto").value.toString()
                        var hayrostro:String = postSnapshot1.child("hayrostro").value.toString()
                        var h: Foto = Foto(nombre,key,urlFoto,hayrostro)
                        misFotos.add(h)
                    }
                }
                val mensaje:String = "Dato cargados: "+misFotos.size
                Toast.makeText(applicationContext,mensaje,Toast.LENGTH_LONG).show()
                adapter = MyAdapter(applicationContext, misFotos)
                binding.lvmishuellasfirebase.adapter = adapter


                val vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrator.vibrate(500)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }

        databaseReference.addValueEventListener(escucha)

        binding.lvmishuellasfirebase.setOnItemClickListener { parent,view, position, id ->
            val mensaje = "Foto: "+misFotos[position].nombre + "==>" +misFotos[position].hayrostro
            Toast.makeText(applicationContext, mensaje, Toast.LENGTH_LONG).show()
        }

        binding.lvmishuellasfirebase.setOnItemLongClickListener(OnItemLongClickListener { arg0, v, index, arg3 ->
            val dbref = FirebaseDatabase.getInstance().getReference().child("Foto")
            val query: Query = dbref
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(@Nonnull dataSnapshot: DataSnapshot) {
                    for (postSnapshot in dataSnapshot.children) {
                        for (postSnapshot1 in postSnapshot.children){
                            var key:String = postSnapshot.child("key").value.toString()
                            if (key == misFotos[index].key){
                                postSnapshot.ref.removeValue()
                                break
                            }
                        }
                    }
                    Toast.makeText(applicationContext, "Foto eliminada", Toast.LENGTH_LONG).show()
                }

                override fun onCancelled(@Nonnull databaseError: DatabaseError) {}
            })
            true
        })

        val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                Miuri = result.data?.data
                Miuri?.let {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                    mBitmap = bitmap
                    binding.imgFoto.setImageBitmap(bitmap)
                }
            }
        }

        binding.btnseleccionar.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                galleryLauncher.launch(galleryIntent)
            }
        })

        // Reemplazar el comportamiento de btncargar para tomar una foto con la cámara
        binding.btncamara.setOnClickListener {
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

                // Guardar el Bitmap en un archivo temporal
                val tempFile = saveBitmapToFile(bitmap)

                // Obtener la URI del archivo
                Miuri = Uri.fromFile(tempFile)
            }
        }

    private fun saveBitmapToFile(bitmap: Bitmap): File {
        val tempFile = File.createTempFile("photo_${UUID.randomUUID()}", ".jpg", cacheDir)
        val outputStream = FileOutputStream(tempFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return tempFile
    }
}

