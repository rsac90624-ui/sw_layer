package com.example.sw_layer_application.domain.usecase

import com.example.sw_layer_application.domain.model.LayerField
import com.example.sw_layer_application.domain.repository.LayerFieldRepository
import kotlinx.coroutines.flow.Flow

import javax.inject.Inject

class GetFieldsForLayerUseCase @Inject constructor(
    private val repository: LayerFieldRepository
) {

    operator fun invoke(
        layerId: Long
    ): Flow<List<LayerField>> {

        return repository.getFieldsForLayer(
            layerId = layerId
        )
    }
}