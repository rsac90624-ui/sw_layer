package com.example.sw_layer_application.data.local


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "features"
)
data class FeatureEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val layerId: Long,

    val geometryType: String,

    val latitude: Double? = null,

    val longitude: Double? = null,

    val geometryJson: String? = null,

    val attributesJson: String? = null,

    val createdAt: Long
)