package com.example.sw_layer_application.domain.repository

import com.example.sw_layer_application.domain.model.Feature
import kotlinx.coroutines.flow.Flow

interface FeatureRepository {

    suspend fun createFeature(
        feature: Feature
    ): Long

    fun getFeaturesForLayer(
        layerId: Long
    ): Flow<List<Feature>>

    suspend fun getFeatureById(
        featureId: Long
    ): Feature?

    suspend fun updateFeature(
        feature: Feature
    )

    suspend fun updateFeatureAttributes(
        featureId: Long,
        attributesJson: String
    ): Boolean

    suspend fun deleteFeature(
        featureId: Long
    )
}