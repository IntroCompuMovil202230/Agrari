package com.example.agrari

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.example.agrari.Model.AgrariPost
import com.example.agrari.Model.Chat
import com.example.agrari.Model.Message
import com.example.agrari.databinding.ActivityChatBinding
import com.example.agrari.databinding.ActivityVerPublicacionBinding
import com.example.agrari.services.AuthService
import com.example.agrari.services.DB_Service

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    var sensorManager: SensorManager? = null
    var lightSensor: Sensor? = null
    var lightSensorListener: SensorEventListener? = null
    lateinit var user_uid:String
    lateinit var seller_uid:String
    lateinit var authService: AuthService
    private  lateinit var dbService: DB_Service
    private  lateinit var chat: Chat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authService= AuthService()
        dbService= DB_Service()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)

        user_uid=intent.getStringExtra("user_uid")!!
        seller_uid=intent.getStringExtra("seller_uid")!!


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



        dbService.getChat(user_uid,seller_uid).addSnapshotListener { value, error ->

            Log.w("LISTENER-DATA", "Listening the data...")

            if (error != null) {
                Log.w("LISTENER-ERROR", "Listen failed.", error)
                return@addSnapshotListener
            }

            for (doc in value!!) {
                Log.i("CHAT-DATA:", "USER_ID: ${doc.getString("uid_user")} SELLER_ID: ${doc.getString("uid_seller")} ")
                chat= Chat(doc)
                break
            }


            //terrenosGrid.adapter= PublicacionAdapter(this,posts)

            binding.messagesList.adapter = MessageAdapter(this,chat.messages)

        }


        binding.send.setOnClickListener {
            Log.w("MESSAGE INFO", "AUTHOR_ID : ${authService.getCurrentUser()!!.uid} MESSAGE: ${binding.messageBox.text}")
            this.chat.messages.add(Message(authService.getCurrentUser()!!.uid,binding.messageBox.text.toString()))
            this.dbService.updateChat(this.chat)
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
}