package com.example.agrari

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class VerPublicacionActivity : AppCompatActivity() {
    lateinit var publicacion: Publicacion
    lateinit var publicacionTitulo: TextView
    lateinit var publicacionUbicacion: TextView
    lateinit var publicacionPrecio: TextView
    lateinit var publicacionImagen: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_publicacion)
        publicacion= intent.getSerializableExtra("publicacion") as Publicacion
        publicacionImagen=findViewById(R.id.infoPublicacionImagen)
        publicacionTitulo= findViewById(R.id.infoPublicacionTitulo)
        publicacionUbicacion= findViewById(R.id.infoPublicacionUbicacion)
        publicacionPrecio= findViewById(R.id.infoPublicacionPrecio)
        publicacionTitulo.text= publicacion.titulo
        publicacionUbicacion.text= publicacion.ubicacion
        publicacionPrecio.text= " $ "+publicacion.precio
        Picasso.get().load(publicacion.imagen).into(publicacionImagen)

    }
}