package com.example.agrari.services

import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import java.io.IOException

class GeoCoderService(geocoder: Geocoder) {

    var mGeocoder: Geocoder

    init {
        this.mGeocoder = geocoder
    }

    fun addressStringToLatLong(addressString: String): LatLng? {
        Log.i("GEOCODER-INPUT", "SERACH SEND $addressString")
        try {
            val addresses: List<Address>? = mGeocoder.getFromLocationName(addressString, 2)
            return if (addresses != null && addresses.isNotEmpty()) {
                val addressResult: Address = addresses[0]
                val position = LatLng(addressResult.latitude, addressResult.longitude)
                Log.i("GEOCODER-STRING-TO-LATLONG", "Latitud: " + position.latitude)
                Log.i("GEOCODER-STRING-TO-LATLONG", "Longitud: " + position.longitude)
                position
            } else {
                return null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
    fun latLongToAddressString(coordinates: LatLng): String{
        var address = mGeocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1)
        Log.i("GEOCODER-LATLONG-TO-STRING", "ADDRESS: " + address!![0].getAddressLine(0))
        return address[0].getAddressLine(0)
    }


    fun getDepartamentoFromLatLong(coordinates: LatLng): String{
        var address = mGeocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1)
        Log.i("GEOCODER-LATLONG-TO-STRING", "ADDRESS: " + address!![0].getAddressLine(0))
        var add_info =address[0].getAddressLine(0).split(",")
        Log.i("GEOCODER-LATLONG-TO-STRING", "DEPARTAMENTO: " + add_info.get(add_info.size-2))
        return add_info.get(add_info.size-2)
    }


}