package com.example.sw_layer_application.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "layer_fields",
    foreignKeys = [
        ForeignKey(
            entity = LayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["layerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["layerId"])
    ]
)
data class LayerFieldEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val layerId: Long,

    val name: String,

    val fieldType: String,

    val isRequired: Boolean,

    val createdAt: Long
)