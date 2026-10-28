package com.example.sw_layer_application.core.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun hasLocationPermission(): Boolean {

        val fineLocationGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        return fineLocationGranted || coarseLocationGranted
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getLastKnownLocation(): Location? {

        if (!hasLocationPermission()) {
            return null
        }

        val gpsLocation =
            locationManager.getLastKnownLocation(
                LocationManager.GPS_PROVIDER
            )

        val networkLocation =
            locationManager.getLastKnownLocation(
                LocationManager.NETWORK_PROVIDER
            )

        return when {

            gpsLocation == null && networkLocation == null -> {
                null
            }

            gpsLocation == null -> {
                networkLocation
            }

            networkLocation == null -> {
                gpsLocation
            }

            gpsLocation.time >= networkLocation.time -> {
                gpsLocation
            }

            else -> {
                networkLocation
            }
        }
    }
}