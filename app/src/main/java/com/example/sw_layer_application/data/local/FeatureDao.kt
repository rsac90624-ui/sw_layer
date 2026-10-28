package com.example.sw_layer_application.data.local


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FeatureDao {

    @Insert
    suspend fun insertFeature(
        feature: FeatureEntity
    ): Long

    @Query(
        """
        SELECT *
        FROM features
        WHERE layerId = :layerId
        ORDER BY createdAt DESC
        """
    )
    fun getFeaturesForLayer(
        layerId: Long
    ): Flow<List<FeatureEntity>>

    @Query(
        """
        SELECT *
        FROM features
        WHERE id = :featureId
        LIMIT 1
        """
    )
    suspend fun getFeatureById(
        featureId: Long
    ): FeatureEntity?

    @Query(
        """
        DELETE FROM features
        WHERE id = :featureId
        """
    )
    suspend fun deleteFeature(
        featureId: Long
    )

    @Update
    suspend fun updateFeature(
        feature: FeatureEntity
    )

    @Query("""
    UPDATE features
    SET attributesJson = :attributesJson
    WHERE id = :featureId
""")
    suspend fun updateFeatureAttributes(
        featureId: Long,
        attributesJson: String
    ): Int
}