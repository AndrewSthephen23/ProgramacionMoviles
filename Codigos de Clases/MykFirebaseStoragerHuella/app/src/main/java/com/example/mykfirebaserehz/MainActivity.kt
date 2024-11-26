package com.example.mykfirebaserehz

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.mykfirebaserehz.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import com.google.firebase.database.DatabaseError
import android.util.Log
import android.widget.AdapterView.OnItemLongClickListener
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.Query
import androidx.annotation.NonNull
import com.example.mykfirebaserehz.HuggingFace.RequestData
import com.example.mykfirebaserehz.HuggingFace.RetrofitHuella
import java.util.UUID
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import retrofit2.Call
//import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    var mishuellas = ArrayList<Huella>()
    lateinit var binding: ActivityMainBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit  var firebaseDatabase: FirebaseDatabase

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
                if(Miuri!=null){
                    var reference:StorageReference = storageReference.child("imghuellas/"+ UUID.randomUUID().toString())

                    reference.putFile(Miuri!!).addOnSuccessListener {
                        reference.downloadUrl.addOnSuccessListener { uri: Uri ->

                            val miURL:String = uri.toString()
                            val requestData = RequestData(miURL)
                            val nombre = binding.txtnombre.getText().toString()
                            val tipoHuella:String = "TipoX"
                            val key = databaseReference.child("Huella").push().getKey()
                            val c = Huella(nombre,  key.toString(),miURL,tipoHuella)
                            databaseReference.child("Huella").push().setValue(c)
                            val mensaje:String= "Tipo Huella: "+tipoHuella
                            Toast.makeText(applicationContext,mensaje,Toast.LENGTH_LONG).show()

                            binding.txtnombre.setText("")
                            val call = RetrofitHuella.getinstance.predict(miURL)
                            /*call.enqueue(object : Callback<ResponseData> {
                                override fun onResponse(
                                    call: Call<ResponseData>,
                                    response: Response<ResponseData>
                                ) {
                                    if (response.isSuccessful) {
                                        val responseData = response.body()
                                        responseData?.let {
                                            val nombre = binding.txtnombre.getText().toString()
                                            val tipoHuella:String = it.prediction
                                            val key = databaseReference.child("Huella").push().getKey()
                                            val c = Huella(nombre,  key.toString(),miURL,tipoHuella)
                                            databaseReference.child("Huella").push().setValue(c)
                                            val mensaje:String= "Tipo Huella: "+tipoHuella
                                            Toast.makeText(applicationContext,mensaje,Toast.LENGTH_LONG).show()
                                            val myToast = Toast.makeText(applicationContext,mensaje,Toast.LENGTH_SHORT)
                                            myToast.show()
                                            binding.txtnombre.setText("")
                                        }
                                    } else {
                                        val myToast = Toast.makeText(applicationContext,"Error1",Toast.LENGTH_SHORT)
                                        myToast.show()
                                    }
                                }

                                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                                    val myToast = Toast.makeText(applicationContext,"Error2",Toast.LENGTH_SHORT)
                                    myToast.show()
                                }
                            })


                            */

                        }.addOnFailureListener { exception ->
                            Toast.makeText(applicationContext,"Image Retrived Failed: "+exception.message,Toast.LENGTH_LONG).show()

                        }

                    }.addOnFailureListener {

                        Toast.makeText(applicationContext, "Fallo en subir imagen", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        })

        val escucha = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mishuellas.clear()
                for (postSnapshot in dataSnapshot.children) {
                    for (postSnapshot1 in postSnapshot.children) {
                        var nombre:String =postSnapshot1.child("nombre").value.toString()
                        var key:String =postSnapshot1.child("key").value.toString()
                        val urlHuella:String = postSnapshot1.child("urlHuella").value.toString()
                        var tipoHuella:String =postSnapshot1.child("tipoHuella").value.toString()
                        var h: Huella = Huella(nombre,key,urlHuella,tipoHuella)
                        mishuellas.add(h)
                    }
                }
                val mensaje:String= "Dato cargados: "+mishuellas.size
                Toast.makeText(applicationContext,mensaje,Toast.LENGTH_LONG).show()
                adapter = MyAdapter(applicationContext,mishuellas)
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
        binding.lvmishuellasfirebase.setOnItemClickListener { parent, view, position, id ->
            val mensaje:String= "Huella: "+mishuellas[position].nombre +" ==> "+ mishuellas[position].tipoHuella
            Toast.makeText(applicationContext,mensaje,Toast.LENGTH_LONG).show()
        }
        binding.lvmishuellasfirebase.setOnItemLongClickListener(OnItemLongClickListener { arg0, v, index, arg3 ->
            val dbref = FirebaseDatabase.getInstance().getReference().child("Huella")
            val query: Query = dbref
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                    for (postSnapshot in dataSnapshot.children) {
                        for (postSnapshot1 in postSnapshot.children) {
                            var key:String =postSnapshot.child("key").value.toString()
                            if (key == mishuellas[index].key){
                                postSnapshot.ref.removeValue()
                                break
                            }
                        }
                    }
                }
                override fun onCancelled(@NonNull databaseError: DatabaseError) {}
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
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

         */
    }
}