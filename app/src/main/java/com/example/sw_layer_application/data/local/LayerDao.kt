package com.example.sw_layer_application.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LayerDao {

    @Insert
    suspend fun insertLayer(
        layer: LayerEntity
    ): Long

    @Query(
        """
        SELECT *
        FROM layers
        WHERE projectId = :projectId
        ORDER BY createdAt DESC
        """
    )
    fun getLayersForProject(
        projectId: Long
    ): Flow<List<LayerEntity>>

    @Query(
        """
        SELECT *
        FROM layers
        WHERE id = :layerId
        LIMIT 1
        """
    )
    suspend fun getLayerById(
        layerId: Long
    ): LayerEntity?

    @Query(
        """
        DELETE FROM layers
        WHERE id = :layerId
        """
    )
    suspend fun deleteLayer(
        layerId: Long
    )
}