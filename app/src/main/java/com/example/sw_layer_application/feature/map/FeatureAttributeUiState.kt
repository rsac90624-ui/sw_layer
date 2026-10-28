package com.example.sw_layer_application.feature.map

data class FeatureAttributeUiState(
    val values: Map<String, String> = emptyMap(),
    val errorMessage: String? = null
)