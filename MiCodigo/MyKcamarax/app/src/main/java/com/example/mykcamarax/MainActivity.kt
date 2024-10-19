package com.example.mykcamarax

import android.app.Activity
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mykcamarax.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val cameraPermissionRequestCode = 100
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btniniciar.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                handleCameraPermission()
            }
        })

    }
    private fun startDefaultCamera(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                //La aplicación de la cámara está disponible: iníciala
                takePictureLauncher.launch(takePictureIntent)
            } ?: run {
                // No hay aplicación de cámara disponible: informar al usuario
                Toast.makeText(this,"No hay aplicación de cámara disponible", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun handleCameraPermission(){
        when{
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED ->{
                // El permiso ya está concedido: inicie la cámara
                startDefaultCamera()
            }

            else ->{
                // El permiso no está concedido: solicítelo
                cameraPermissionRequestLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
    private val cameraPermissionRequestLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted: Boolean ->
            if (isGranted){
                // Permiso concedido: proceda a abrir la cámara
                startDefaultCamera()
            } else{
                // Permiso denegado: informar al usuario para que lo habilite a través de los ajustes.
                Toast.makeText(
                    this,
                    "Vaya a la configuración y habilite el permiso de la cámara para utilizar esta función",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    private val takePictureLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            // Esto se puede ampliar para manejar los datos de resultado
            Toast.makeText(this,"Foto tomada", Toast.LENGTH_LONG).show()
        }
    object CameraPermissionHelper{
        private const val CAMERA_PERMISSION_CODE = 0
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA

        // Comprueba que tenemos los permisos necesarios para esta aplicación.
        fun hasCameraPermission(activity: Activity): Boolean{
            return ContextCompat.checkSelfPermission(activity, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED
        }

        // Comprueba que tenemos los permisos necesarios para esta aplicación, y pídelos si no los tenemos.
        fun requestCameraPermission(activity: Activity){
            ActivityCompat.requestPermissions(
                activity, arrayOf(CAMERA_PERMISSION), CAMERA_PERMISSION_CODE)
        }

        // Compruebe si es necesario mostrar la justificación de este permiso.
        fun shouldShowRequestPermissionRationale(activity: Activity): Boolean{
            return ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA_PERMISSION)
        }

        // Inicie la configuración de la aplicación para conceder el permiso.
        fun launchPermissionSettings(activity: Activity){
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.fromParts("package",activity.packageName,null)
            activity.startActivity(intent)
        }

    }
}