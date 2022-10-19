package com.example.mapstest

import android.Manifest
import android.R.attr.elevation
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.*
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task


class LocationController(currentActivity: Activity,locationManager:LocationManager) {

    var mFusedLocationClient: FusedLocationProviderClient
    var currentActivity: Activity
    var locationManager: LocationManager
    var mLocationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    init {
        this.locationManager=locationManager
        this.currentActivity= currentActivity
        this.mFusedLocationClient= LocationServices.getFusedLocationProviderClient(this.currentActivity)
        this.mLocationRequest= createLocationRequest()
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    fun getCurrentLocation():Task<Location>{
        return this.mFusedLocationClient.lastLocation
    }


     fun checkLocationSettings():Task<LocationSettingsResponse>{
        val locationRequest = LocationRequest.create()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this.currentActivity)
        val task = client.checkLocationSettings(builder.build())
        return task
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    fun startCurrentLocationUpdates(locationCallback:LocationCallback){
        this.locationCallback=locationCallback
        this.mFusedLocationClient.requestLocationUpdates(
            this.mLocationRequest,
            this.locationCallback,
            Looper.getMainLooper())
    }

    fun stopLocationUpdates() {
        this.mFusedLocationClient.removeLocationUpdates(this.locationCallback);
    }


}
