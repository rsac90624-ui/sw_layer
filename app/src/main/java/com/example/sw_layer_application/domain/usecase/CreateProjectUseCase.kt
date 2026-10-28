package com.example.sw_layer_application.domain.usecase


import com.example.sw_layer_application.domain.model.Project
import com.example.sw_layer_application.domain.repository.ProjectRepository

class CreateProjectUseCase(
    private val repository: ProjectRepository
) {

    suspend operator fun invoke(name: String): Long {

        val cleanName = name.trim()

        require(cleanName.isNotEmpty()) {
            "Project name cannot be empty"
        }

        val project = Project(
            name = cleanName,
            createdAt = System.currentTimeMillis()
        )

        return repository.createProject(project)
    }
}