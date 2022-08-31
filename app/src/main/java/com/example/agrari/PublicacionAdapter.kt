package com.example.agrari

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class PublicacionAdapter (private  val mContext: Context, private val publicacionList: List<Publicacion>):
    ArrayAdapter<Publicacion>(mContext,0,publicacionList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val ly= LayoutInflater.from(mContext).inflate(R.layout.publicacion_item,parent,false)
        var publicacion= publicacionList[position]
        var imagen: ImageView=ly.findViewById<ImageView>(R.id.publicacionImagen)
        Picasso.get().load(publicacion.imagen).into(imagen)
        ly.findViewById<TextView>(R.id.publicacionTitulo).text=publicacion.titulo
        ly.findViewById<TextView>(R.id.publicacionUbicacion).text=publicacion.ubicacion
        ly.findViewById<TextView>(R.id.publicacionPrecio).text=publicacion.precio.toString()
        return ly
    }
}