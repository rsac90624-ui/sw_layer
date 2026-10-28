package com.example.sw_layer_application.feature.map

import com.example.sw_layer_application.domain.model.Project



data class MapUiState(
    val projects: List<Project> = emptyList(),
    val activeProject: Project? = null,
    val isLoading: Boolean = true,

    // Location
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationError: String? = null
)