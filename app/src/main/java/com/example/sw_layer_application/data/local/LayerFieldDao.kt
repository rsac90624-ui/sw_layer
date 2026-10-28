package com.example.sw_layer_application.data.local


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LayerFieldDao {

    @Insert
    suspend fun insertField(
        field: LayerFieldEntity
    ): Long

    @Query(
        """
        SELECT *
        FROM layer_fields
        WHERE layerId = :layerId
        ORDER BY createdAt ASC
        """
    )
    fun getFieldsForLayer(
        layerId: Long
    ): Flow<List<LayerFieldEntity>>

    @Query(
        """
        DELETE FROM layer_fields
        WHERE id = :fieldId
        """
    )
    suspend fun deleteField(
        fieldId: Long
    )
}