package com.example.sw_layer_application.feature.projects


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sw_layer_application.domain.usecase.CreateProjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val createProjectUseCase: CreateProjectUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(ProjectUiState())

    val uiState: StateFlow<ProjectUiState> =
        _uiState.asStateFlow()

    fun onProjectNameChange(
        name: String
    ) {

        _uiState.update {
            it.copy(
                projectName = name,
                errorMessage = null
            )
        }
    }

    fun createProject() {

        val projectName =
            _uiState.value.projectName.trim()

        if (projectName.isEmpty()) {

            _uiState.update {
                it.copy(
                    errorMessage = "Enter project name"
                )
            }

            return
        }

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isCreating = true,
                    errorMessage = null
                )
            }

            try {

                val projectId =
                    createProjectUseCase(projectName)

                _uiState.update {
                    it.copy(
                        isCreating = false,
                        projectCreated = true,
                        createdProjectId = projectId
                    )
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isCreating = false,
                        errorMessage =
                            e.message ?: "Unable to create project"
                    )
                }
            }
        }
    }
}