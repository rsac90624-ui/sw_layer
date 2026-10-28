package com.example.sw_layer_application.domain.usecase

import com.example.sw_layer_application.domain.model.Project
import com.example.sw_layer_application.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow

class GetProjectsUseCase(
    private val repository: ProjectRepository
) {

    operator fun invoke(): Flow<List<Project>> {
        return repository.getProjects()
    }
}