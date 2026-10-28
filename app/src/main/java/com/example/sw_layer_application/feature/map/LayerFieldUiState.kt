package com.example.sw_layer_application.feature.map


import com.example.sw_layer_application.domain.model.FieldType
import com.example.sw_layer_application.domain.model.Layer
import com.example.sw_layer_application.domain.model.LayerField

data class LayerFieldUiState(

    val selectedLayer: Layer? = null,

    val fields: List<LayerField> = emptyList(),

    val fieldName: String = "",

    val selectedFieldType: FieldType =
        FieldType.TEXT,

    val isRequired: Boolean = false,

    val isCreatingField: Boolean = false,

    val errorMessage: String? = null
)