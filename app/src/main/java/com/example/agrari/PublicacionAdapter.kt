package com.example.agrari

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.agrari.Model.AgrariPost
import com.example.taller3_compu_movil.controller.ImageEncodingController
import com.squareup.picasso.Picasso

class PublicacionAdapter (private  val mContext: Context, private val publicacionList: List<AgrariPost>):
    ArrayAdapter<AgrariPost>(mContext,0,publicacionList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val ly= LayoutInflater.from(mContext).inflate(R.layout.publicacion_item,parent,false)
        var post= publicacionList[position]
        var imageEncodingController= ImageEncodingController()
        var imagen: ImageView=ly.findViewById<ImageView>(R.id.publicacionImagen)
        imagen.setImageBitmap(imageEncodingController.decodeImage(post.image))
        ly.findViewById<TextView>(R.id.publicacionTitulo).text=post.title
        ly.findViewById<TextView>(R.id.publicacionUbicacion).text=post.departamento
        ly.findViewById<TextView>(R.id.publicacionPrecio).text=post.price.toString()
        return ly
    }
}