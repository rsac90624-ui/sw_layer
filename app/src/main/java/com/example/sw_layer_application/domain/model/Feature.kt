package com.example.sw_layer_application.domain.model


data class Feature(
    val id: Long = 0L,
    val layerId: Long,
    val geometryType: GeometryType,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val geometryJson: String? = null,
    val attributesJson: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)