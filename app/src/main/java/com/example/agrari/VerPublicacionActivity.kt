package com.example.agrari

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.agrari.databinding.ActivityVerPublicacionBinding
import com.google.android.gms.maps.model.LatLng
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.example.agrari.Model.AgrariPost
import com.example.agrari.Model.Chat
import com.example.agrari.services.AuthService
import com.example.taller3_compu_movil.controller.ImageEncodingController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_ver_publicacion.*
import java.util.*

class VerPublicacionActivity : AppCompatActivity() {
    lateinit var publicacion: AgrariPost
    private lateinit var binding: ActivityVerPublicacionBinding
    var sensorManager: SensorManager? = null
    var lightSensor: Sensor? = null
    var lightSensorListener: SensorEventListener? = null
    lateinit var imageEncodingController: ImageEncodingController
    lateinit var authService: AuthService

    private var user = ""
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerPublicacionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        publicacion= intent.getSerializableExtra("post") as AgrariPost
        authService= AuthService()
        imageEncodingController= ImageEncodingController()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)


        binding.infoPublicacionTitulo.text= publicacion.title
        binding.infoPublicacionUbicacion.text= publicacion.departamento
        binding.infoPublicacionPrecio.text= " $ "+publicacion.price
        binding.infoPublicacionImagen.setImageBitmap(imageEncodingController.decodeImage(publicacion.image))


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


        binding.verDistanciaMapaButton.setOnClickListener {
            var intent= Intent(it.context,DistanceToTerrenoActivity::class.java)
            intent.putExtra("post",publicacion)
            startActivity(intent)
        }

        binding.enviarMensajeButton.setOnClickListener {
            Log.w("IDS-FOR-MESSAGE", "UserUID: ${authService.getCurrentUser()!!.uid}, ${this.publicacion.uid}")

            newChat()
        }

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

    private fun newChat(){
        val chatId = UUID.randomUUID().toString()
        val otherUser = publicacion.seller_uid
        val desc = infoPublicacionTitulo.text.toString()

        val chat = Chat(
            id = chatId,
            user = "Chat con $otherUser",
            description = desc
        )

        db.collection("chats").document(chatId).set(chat)
        db.collection("users").document(user).collection("chats").document(chatId).set(chat)
        db.collection("users").document(otherUser).collection("chats").document(chatId).set(chat)

        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("chatId", chatId)
        intent.putExtra("user", user)
        intent.putExtra("desc", desc)
        startActivity(intent)
    }
}