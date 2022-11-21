package com.example.agrari.controller

import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONObject

class RouteController {


    fun parse(jObject: JSONObject):MutableList<LatLng>{
        val routes: MutableList<List<HashMap<String, String>>> = ArrayList()
        val path: MutableList<LatLng> = ArrayList()

        val routesList: JSONArray =(((
            jObject.get("features")as JSONArray)
            .get(0) as JSONObject)
            .get("geometry") as JSONObject)
            .getJSONArray("coordinates")

        for (i in 0 until routesList.length()) {
            val route = routesList.getJSONArray(i)
            path.add(LatLng(route[1].toString().toDouble(),route[0].toString().toDouble()))

        }
        return path
    }


}