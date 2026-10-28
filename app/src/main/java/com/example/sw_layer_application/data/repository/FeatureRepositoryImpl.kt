package com.example.sw_layer_application.data.repository

import com.example.sw_layer_application.data.local.FeatureDao
import com.example.sw_layer_application.data.local.FeatureEntity
import com.example.sw_layer_application.domain.model.Feature
import com.example.sw_layer_application.domain.model.GeometryType
import com.example.sw_layer_application.domain.repository.FeatureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FeatureRepositoryImpl @Inject constructor(
    private val featureDao: FeatureDao
) : FeatureRepository {

    override suspend fun createFeature(
        feature: Feature
    ): Long {

        return featureDao.insertFeature(
            FeatureEntity(
                id = feature.id,
                layerId = feature.layerId,
                geometryType =
                    feature.geometryType.name,
                latitude =
                    feature.latitude,
                longitude =
                    feature.longitude,
                geometryJson =
                    feature.geometryJson,
                attributesJson =
                    feature.attributesJson,
                createdAt =
                    feature.createdAt
            )
        )
    }

    override fun getFeaturesForLayer(
        layerId: Long
    ): Flow<List<Feature>> {

        return featureDao
            .getFeaturesForLayer(layerId)
            .map { entities ->

                entities.map { entity ->

                    Feature(
                        id = entity.id,
                        layerId = entity.layerId,
                        geometryType =
                            GeometryType.valueOf(
                                entity.geometryType
                            ),
                        latitude =
                            entity.latitude,
                        longitude =
                            entity.longitude,
                        geometryJson =
                            entity.geometryJson,
                        attributesJson =
                            entity.attributesJson,
                        createdAt =
                            entity.createdAt
                    )
                }
            }
    }

    override suspend fun getFeatureById(
        featureId: Long
    ): Feature? {

        val entity =
            featureDao.getFeatureById(featureId)
                ?: return null

        return Feature(
            id = entity.id,
            layerId = entity.layerId,
            geometryType =
                GeometryType.valueOf(
                    entity.geometryType
                ),
            latitude = entity.latitude,
            longitude = entity.longitude,
            geometryJson = entity.geometryJson,
            attributesJson = entity.attributesJson,
            createdAt = entity.createdAt
        )
    }

    override suspend fun updateFeature(
        feature: Feature
    ) {

        featureDao.updateFeature(
            FeatureEntity(
                id = feature.id,
                layerId = feature.layerId,
                geometryType =
                    feature.geometryType.name,
                latitude = feature.latitude,
                longitude = feature.longitude,
                geometryJson = feature.geometryJson,
                attributesJson = feature.attributesJson,
                createdAt = feature.createdAt
            )
        )
    }

    override suspend fun updateFeatureAttributes(
        featureId: Long,
        attributesJson: String
    ): Boolean {

        return featureDao.updateFeatureAttributes(
            featureId = featureId,
            attributesJson = attributesJson
        ) > 0
    }

    override suspend fun deleteFeature(
        featureId: Long
    ) {
        featureDao.deleteFeature(
            featureId
        )
    }



}