package com.example.sw_layer_application.domain.usecase

import com.example.sw_layer_application.domain.repository.LayerFieldRepository
import javax.inject.Inject

class DeleteLayerFieldUseCase @Inject constructor(
    private val repository: LayerFieldRepository
) {

    suspend operator fun invoke(
        fieldId: Long
    ) {

        repository.deleteField(
            fieldId
        )
    }
}