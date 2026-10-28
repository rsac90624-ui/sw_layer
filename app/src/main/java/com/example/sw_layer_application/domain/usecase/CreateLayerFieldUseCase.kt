package com.example.sw_layer_application.domain.usecase

import com.example.sw_layer_application.domain.model.FieldType
import com.example.sw_layer_application.domain.model.LayerField
import com.example.sw_layer_application.domain.repository.LayerFieldRepository
import javax.inject.Inject

class CreateLayerFieldUseCase @Inject constructor(
    private val repository: LayerFieldRepository
) {

    suspend operator fun invoke(
        layerId: Long,
        name: String,
        fieldType: FieldType,
        isRequired: Boolean
    ): Long {

        return repository.createField(
            LayerField(
                layerId = layerId,
                name = name.trim(),
                fieldType = fieldType,
                isRequired = isRequired,
                createdAt = System.currentTimeMillis()
            )
        )
    }
}