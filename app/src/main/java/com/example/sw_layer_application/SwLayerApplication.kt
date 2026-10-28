package com.example.sw_layer_application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.maplibre.android.MapLibre

@HiltAndroidApp
class SwLayerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        MapLibre.getInstance(this)
    }
}