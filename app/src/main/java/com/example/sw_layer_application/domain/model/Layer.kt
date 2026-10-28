package com.example.sw_layer_application.domain.model

data class Layer(
    val id: Long = 0,
    val projectId: Long,
    val name: String,
    val geometryType: GeometryType,
    val createdAt: Long
)