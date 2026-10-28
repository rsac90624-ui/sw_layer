package com.example.sw_layer_application.data.mapper

import com.example.sw_layer_application.data.local.LayerEntity
import com.example.sw_layer_application.domain.model.GeometryType
import com.example.sw_layer_application.domain.model.Layer

fun LayerEntity.toDomain(): Layer {

    return Layer(
        id = id,
        projectId = projectId,
        name = name,
        geometryType = GeometryType.valueOf(geometryType),
        createdAt = createdAt
    )
}

fun Layer.toEntity(): LayerEntity {

    return LayerEntity(
        id = id,
        projectId = projectId,
        name = name,
        geometryType = geometryType.name,
        createdAt = createdAt
    )
}