package com.example.sw_layer_application.data.mapper


import com.example.sw_layer_application.data.local.ProjectEntity
import com.example.sw_layer_application.domain.model.Project

fun ProjectEntity.toDomain(): Project {

    return Project(
        id = id,
        name = name,
        createdAt = createdAt
    )
}

fun Project.toEntity(): ProjectEntity {

    return ProjectEntity(
        id = id,
        name = name,
        createdAt = createdAt
    )
}