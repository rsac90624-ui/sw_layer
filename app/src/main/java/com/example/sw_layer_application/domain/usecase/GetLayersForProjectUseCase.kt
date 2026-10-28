package com.example.sw_layer_application.domain.usecase


import com.example.sw_layer_application.domain.model.Layer
import com.example.sw_layer_application.domain.repository.LayerRepository
import kotlinx.coroutines.flow.Flow

class GetLayersForProjectUseCase(
    private val repository: LayerRepository
) {

    operator fun invoke(
        projectId: Long
    ): Flow<List<Layer>> {

        return repository.getLayersForProject(
            projectId = projectId
        )
    }
}