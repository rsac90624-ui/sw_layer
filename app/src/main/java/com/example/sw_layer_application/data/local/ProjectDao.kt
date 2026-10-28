package com.example.sw_layer_application.data.local


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Insert
    suspend fun insertProject(
        project: ProjectEntity
    ): Long

    @Query(
        """
        SELECT *
        FROM projects
        ORDER BY createdAt DESC
        """
    )
    fun getProjects(): Flow<List<ProjectEntity>>

    @Query(
        """
        SELECT *
        FROM projects
        WHERE id = :id
        LIMIT 1
        """
    )
    suspend fun getProjectById(
        id: Long
    ): ProjectEntity?
}