package com.example.taller3_compu_movil.controller

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

class ImageEncodingController {


    @RequiresApi(Build.VERSION_CODES.P)
    fun encodeImage(cr: ContentResolver, uri:Uri?):String{
        if(uri==null){
            throw Exception("Choose a valid image")
        }
        //var bm: Bitmap = MediaStore.Images.Media.getBitmap(cr, uri)
        val source: ImageDecoder.Source = ImageDecoder.createSource(cr, uri)
        val bm: Bitmap = ImageDecoder.decodeBitmap(source)
        //Log.i("IMAGE ENCODED: ", "$bm")
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos) // bm is the bitmap object
        val b: ByteArray = baos.toByteArray()
        var encodedImage:String = Base64.encodeToString(b, Base64.DEFAULT)
        //Log.i("IMAGE ENCODED: ", "$encodedImage")
        return encodedImage
    }


    fun decodeImage(data:String):Bitmap{
        val decodedString: ByteArray = Base64.decode(data, Base64.NO_WRAP)
        val inputStream: InputStream = ByteArrayInputStream(decodedString)
        val bitmap:Bitmap= BitmapFactory.decodeStream(inputStream)
        return bitmap
        //binding.profileImage.setImageBitmap(bitmap)
    }





}