package com.example.sw_layer_application.data.repository

import com.example.sw_layer_application.data.local.LayerDao
import com.example.sw_layer_application.data.mapper.toDomain
import com.example.sw_layer_application.data.mapper.toEntity
import com.example.sw_layer_application.domain.model.Layer
import com.example.sw_layer_application.domain.repository.LayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LayerRepositoryImpl(
    private val layerDao: LayerDao
) : LayerRepository {

    override suspend fun createLayer(
        layer: Layer
    ): Long {

        return layerDao.insertLayer(
            layer.toEntity()
        )
    }

    override fun getLayersForProject(
        projectId: Long
    ): Flow<List<Layer>> {

        return layerDao
            .getLayersForProject(projectId)
            .map { entities ->

                entities.map { entity ->
                    entity.toDomain()
                }
            }
    }

    override suspend fun getLayerById(
        layerId: Long
    ): Layer? {

        return layerDao
            .getLayerById(layerId)
            ?.toDomain()
    }

    override suspend fun deleteLayer(
        layerId: Long
    ) {

        layerDao.deleteLayer(layerId)
    }
}