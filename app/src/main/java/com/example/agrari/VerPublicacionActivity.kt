package com.example.agrari

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.agrari.databinding.ActivityVerPublicacionBinding
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso

class VerPublicacionActivity : AppCompatActivity() {
    lateinit var publicacion: Publicacion


    private lateinit var binding: ActivityVerPublicacionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerPublicacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        publicacion= intent.getSerializableExtra("publicacion") as Publicacion

        binding.infoPublicacionTitulo.text= publicacion.titulo
        binding.infoPublicacionUbicacion.text= publicacion.ubicacion
        binding.infoPublicacionPrecio.text= " $ "+publicacion.precio
        Picasso.get().load(publicacion.imagen).into(binding.infoPublicacionImagen)

        binding.verDistanciaMapaButton.setOnClickListener {
            var intent= Intent(it.context,DistanceToTerrenoActivity::class.java)
            intent.putExtra("currentPublicacion",publicacion)
            startActivity(intent)
        }


    }
}