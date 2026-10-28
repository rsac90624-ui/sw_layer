package com.example.sw_layer_application.feature.map


import androidx.lifecycle.ViewModel
import com.example.sw_layer_application.domain.model.Layer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ActiveLayerViewModel @Inject constructor() : ViewModel() {

    private val _uiState =
        MutableStateFlow(ActiveLayerUiState())

    val uiState: StateFlow<ActiveLayerUiState> =
        _uiState.asStateFlow()

    fun setActiveLayer(layer: Layer) {

        _uiState.update {
            it.copy(
                activeLayer = layer
            )
        }
    }

    fun clearActiveLayer() {

        _uiState.value =
            ActiveLayerUiState()
    }
}