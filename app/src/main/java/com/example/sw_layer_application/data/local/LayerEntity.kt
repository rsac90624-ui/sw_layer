package com.example.sw_layer_application.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "layers",

    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],

    indices = [
        Index(value = ["projectId"])
    ]
)
data class LayerEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val projectId: Long,

    val name: String,

    val geometryType: String,

    val createdAt: Long
)