package com.example.sw_layer_application.data.repository


import com.example.sw_layer_application.data.local.ProjectDao
import com.example.sw_layer_application.data.mapper.toDomain
import com.example.sw_layer_application.data.mapper.toEntity
import com.example.sw_layer_application.domain.model.Project
import com.example.sw_layer_application.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProjectRepositoryImpl(
    private val projectDao: ProjectDao
) : ProjectRepository {

    override suspend fun createProject(
        project: Project
    ): Long {

        return projectDao.insertProject(
            project.toEntity()
        )
    }

    override fun getProjects(): Flow<List<Project>> {

        return projectDao
            .getProjects()
            .map { entities ->

                entities.map { entity ->
                    entity.toDomain()
                }
            }
    }

    override suspend fun getProjectById(
        id: Long
    ): Project? {

        return projectDao
            .getProjectById(id)
            ?.toDomain()
    }
}