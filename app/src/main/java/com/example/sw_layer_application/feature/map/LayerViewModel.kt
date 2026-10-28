package com.example.sw_layer_application.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sw_layer_application.domain.model.GeometryType
import com.example.sw_layer_application.domain.usecase.CreateLayerUseCase
import com.example.sw_layer_application.domain.usecase.GetLayersForProjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LayerViewModel @Inject constructor(
    private val createLayerUseCase: CreateLayerUseCase,
    private val getLayersForProjectUseCase: GetLayersForProjectUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LayerUiState())

    val uiState: StateFlow<LayerUiState> =
        _uiState.asStateFlow()

    private var observeLayersJob: Job? = null

    private var currentProjectId: Long? = null

    fun loadLayers(projectId: Long) {

        if (currentProjectId == projectId) {
            return
        }

        currentProjectId = projectId

        observeLayersJob?.cancel()

        observeLayersJob = viewModelScope.launch {

            getLayersForProjectUseCase(projectId)
                .collect { layers ->

                    _uiState.update {
                        it.copy(
                            layers = layers
                        )
                    }
                }
        }
    }

    fun onLayerNameChange(name: String) {

        _uiState.update {
            it.copy(
                layerName = name,
                errorMessage = null
            )
        }
    }

    fun onGeometryTypeChange(
        geometryType: GeometryType
    ) {

        _uiState.update {
            it.copy(
                selectedGeometryType = geometryType
            )
        }
    }

    fun createLayer(
        projectId: Long,
        onSuccess: (Long) -> Unit
    ) {

        val layerName =
            _uiState.value.layerName.trim()

        if (layerName.isEmpty()) {

            _uiState.update {
                it.copy(
                    errorMessage = "Enter layer name"
                )
            }

            return
        }

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isCreatingLayer = true,
                    errorMessage = null
                )
            }

            try {

                val layerId =
                    createLayerUseCase(
                        projectId = projectId,
                        name = layerName,
                        geometryType =
                            _uiState.value.selectedGeometryType
                    )

                _uiState.update {
                    it.copy(
                        layerName = "",
                        selectedGeometryType =
                            GeometryType.POINT,
                        isCreatingLayer = false
                    )
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isCreatingLayer = false,
                        errorMessage =
                            e.message ?: "Unable to create layer"
                    )
                }
            }
        }
    }
}