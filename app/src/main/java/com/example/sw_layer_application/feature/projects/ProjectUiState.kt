package com.example.sw_layer_application.feature.projects


data class ProjectUiState(
    val projectName: String = "",
    val isCreating: Boolean = false,
    val projectCreated: Boolean = false,
    val createdProjectId: Long? = null,
    val errorMessage: String? = null
)