package com.example.sw_layer_application.feature.map

import com.example.sw_layer_application.domain.model.GeometryType
import com.example.sw_layer_application.domain.model.Layer

data class LayerUiState(

    val layers: List<Layer> = emptyList(),

    val layerName: String = "",

    val selectedGeometryType: GeometryType =
        GeometryType.POINT,

    val isCreatingLayer: Boolean = false,

    val errorMessage: String? = null
)