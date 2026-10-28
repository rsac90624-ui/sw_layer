package com.example.sw_layer_application.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sw_layer_application.data.local.FeatureDao
import com.example.sw_layer_application.data.local.FeatureEntity
import com.example.sw_layer_application.data.local.LayerDao
import com.example.sw_layer_application.data.local.LayerEntity
import com.example.sw_layer_application.data.local.LayerFieldDao
import com.example.sw_layer_application.data.local.LayerFieldEntity
import com.example.sw_layer_application.data.local.ProjectDao
import com.example.sw_layer_application.data.local.ProjectEntity

@Database(
    entities = [
        ProjectEntity::class,
        LayerEntity::class,
        LayerFieldEntity::class,
        FeatureEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun projectDao(): ProjectDao

    abstract fun layerDao(): LayerDao

    abstract fun layerFieldDao(): LayerFieldDao

    abstract fun featureDao(): FeatureDao
}