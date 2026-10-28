package com.example.sw_layer_application.domain.usecase


import com.example.sw_layer_application.domain.model.GeometryType
import com.example.sw_layer_application.domain.model.Layer
import com.example.sw_layer_application.domain.repository.LayerRepository

class CreateLayerUseCase(
    private val repository: LayerRepository
) {

    suspend operator fun invoke(
        projectId: Long,
        name: String,
        geometryType: GeometryType
    ): Long {

        val cleanName = name.trim()

        require(cleanName.isNotEmpty()) {
            "Layer name cannot be empty"
        }

        require(projectId > 0) {
            "Invalid project"
        }

        val layer = Layer(
            projectId = projectId,
            name = cleanName,
            geometryType = geometryType,
            createdAt = System.currentTimeMillis()
        )

        return repository.createLayer(layer)
    }
}