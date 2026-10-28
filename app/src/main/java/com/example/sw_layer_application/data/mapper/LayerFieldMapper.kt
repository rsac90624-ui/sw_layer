package com.example.sw_layer_application.data.mapper


import com.example.sw_layer_application.data.local.LayerFieldEntity
import com.example.sw_layer_application.domain.model.FieldType
import com.example.sw_layer_application.domain.model.LayerField

fun LayerFieldEntity.toDomain(): LayerField {

    return LayerField(
        id = id,
        layerId = layerId,
        name = name,
        fieldType = FieldType.valueOf(fieldType),
        isRequired = isRequired,
        createdAt = createdAt
    )
}

fun LayerField.toEntity(): LayerFieldEntity {

    return LayerFieldEntity(
        id = id,
        layerId = layerId,
        name = name,
        fieldType = fieldType.name,
        isRequired = isRequired,
        createdAt = createdAt
    )
}