package com.example.sw_layer_application.domain.repository


import com.example.sw_layer_application.domain.model.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {

    suspend fun createProject(project: Project): Long

    fun getProjects(): Flow<List<Project>>

    suspend fun getProjectById(id: Long): Project?
}