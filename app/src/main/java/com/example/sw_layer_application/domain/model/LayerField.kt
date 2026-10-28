package com.example.sw_layer_application.domain.model


data class LayerField(
    val id: Long = 0,
    val layerId: Long,
    val name: String,
    val fieldType: FieldType,
    val isRequired: Boolean = false,
    val createdAt: Long
)