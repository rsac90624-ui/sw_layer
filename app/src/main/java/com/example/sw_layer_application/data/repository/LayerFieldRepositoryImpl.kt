package com.example.sw_layer_application.data.repository

import com.example.sw_layer_application.data.local.LayerFieldDao
import com.example.sw_layer_application.data.mapper.toDomain
import com.example.sw_layer_application.data.mapper.toEntity
import com.example.sw_layer_application.domain.model.LayerField
import com.example.sw_layer_application.domain.repository.LayerFieldRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LayerFieldRepositoryImpl(
    private val layerFieldDao: LayerFieldDao
) : LayerFieldRepository {

    override suspend fun createField(
        field: LayerField
    ): Long {

        return layerFieldDao.insertField(
            field.toEntity()
        )
    }

    override fun getFieldsForLayer(
        layerId: Long
    ): Flow<List<LayerField>> {

        return layerFieldDao
            .getFieldsForLayer(layerId)
            .map { entities ->

                entities.map { entity ->
                    entity.toDomain()
                }
            }
    }

    override suspend fun deleteField(
        fieldId: Long
    ) {
        layerFieldDao.deleteField(fieldId)
    }
}