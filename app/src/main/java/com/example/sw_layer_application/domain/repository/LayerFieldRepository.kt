package com.example.sw_layer_application.domain.repository


import com.example.sw_layer_application.domain.model.LayerField
import kotlinx.coroutines.flow.Flow

interface LayerFieldRepository {

    suspend fun createField(
        field: LayerField
    ): Long

    fun getFieldsForLayer(
        layerId: Long
    ): Flow<List<LayerField>>

    suspend fun deleteField(
        fieldId: Long
    )
}