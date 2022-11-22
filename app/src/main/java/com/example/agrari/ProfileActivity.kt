package com.example.agrari

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker
import com.example.agrari.databinding.ActivityProfileBinding
import com.example.agrari.databinding.ActivitySignUpRamainInfoBinding
import com.example.agrari.fragments.UploadFragment
import java.io.File

class ProfileActivity : AppCompatActivity() {


    companion object {
        private val CAMERA_CODE = 1004;
        private val GALERY_CODE = 1005;
    }

    lateinit var uriCamera: Uri
    lateinit var binding: ActivityProfileBinding
    var sensorManager: SensorManager? = null
    var lightSensor: Sensor? = null
    var lightSensorListener: SensorEventListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)


        lightSensorListener = object : SensorEventListener {
            override fun onSensorChanged(sensorEvent: SensorEvent) {
                val value = sensorEvent.values[0]
                if (value < 10000) {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
        }

        binding.cameraButtonSignUp.setOnClickListener(View.OnClickListener {
            this.openCamera()
            Log.i("GALERY-CONTROLLER", "ACCESING GALLERY")

        })
        binding.galleryButtonSignUp.setOnClickListener(View.OnClickListener {
            Log.i("CAMERA-CONTROLLER", "ACCESING CAMERA")
            this.openGallery()
        })

    }

    override fun onResume() {
        super.onResume()
        sensorManager!!.registerListener(
            lightSensorListener,
            lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(lightSensorListener)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            GALERY_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    this.selectImageIntent.launch("image/*")
                }
            }
            CAMERA_CODE ->{
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    var file : File = File(filesDir, "picFromCamera");
                    uriCamera = FileProvider.getUriForFile(this, applicationContext.packageName + ".fileprovider", file);
                    this.selectFromCameraIntent.launch(uriCamera)
                }
            }
        }
    }


    ///////GALLERY////////////

    val selectImageIntent = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri ->
        if(uri != null){
            Log.i("GALLERY URI", "The URI IS: $uri")
            binding.profileSignUpPicture.setImageURI(uri)
        }
    }

    private fun openGallery(){
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        when {
            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                this.selectImageIntent.launch("image/*")
            }
            /*shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE) -> {
                this.showSnackBar("Debes aceptar para acceder a la galería",ContextCompat.getColor(this, R.color.teal_700))
                requestPermissions(permissions, GALERY_CODE)
            }*/
            else -> {
                requestPermissions(permissions,GALERY_CODE)
            }
        }
    }


    ///////--------------////////////

    ///////CAMERA////////////
    private val selectFromCameraIntent =
        registerForActivityResult(ActivityResultContracts.TakePicture()){
                bitmap ->
            if (bitmap){
                Log.i("CAMERA CHOOSER","DATA: ${this.uriCamera}")
                binding.profileSignUpPicture.setImageURI(uriCamera)
            }
        }

    private fun openCamera(){
        val permissions = arrayOf(Manifest.permission.CAMERA)
        when {
            checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                var file : File = File(filesDir, "picFromCamera");
                uriCamera = FileProvider.getUriForFile(this, applicationContext.packageName + ".fileprovider", file)
                this.selectFromCameraIntent.launch(uriCamera)
            }
            /*shouldShowRequestPermissionRationale(CAMERA) -> {
                this.showSnackBar("Debes aceptar para acceder a la cámara",ContextCompat.getColor(this, R.color.teal_700))
                requestPermissions(permissions, CAMERA_CODE)
            }*/
            else -> {
                requestPermissions(permissions, CAMERA_CODE)
            }
        }
    }

    ///////--------------////////////



}