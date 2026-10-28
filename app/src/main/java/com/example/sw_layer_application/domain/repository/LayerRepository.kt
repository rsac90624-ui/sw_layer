package com.example.sw_layer_application.domain.repository

import com.example.sw_layer_application.domain.model.Layer
import kotlinx.coroutines.flow.Flow

interface LayerRepository {

    suspend fun createLayer(
        layer: Layer
    ): Long

    fun getLayersForProject(
        projectId: Long
    ): Flow<List<Layer>>

    suspend fun getLayerById(
        layerId: Long
    ): Layer?

    suspend fun deleteLayer(
        layerId: Long
    )
}