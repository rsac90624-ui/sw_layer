package com.example.sw_layer_application.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sw_layer_application.domain.model.FieldType
import com.example.sw_layer_application.domain.model.Layer
import com.example.sw_layer_application.domain.usecase.CreateLayerFieldUseCase
import com.example.sw_layer_application.domain.usecase.DeleteLayerFieldUseCase
import com.example.sw_layer_application.domain.usecase.GetFieldsForLayerUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LayerFieldViewModel @Inject constructor(
    private val createLayerFieldUseCase: CreateLayerFieldUseCase,
    private val getFieldsForLayerUseCase: GetFieldsForLayerUseCase,
    private val deleteLayerFieldUseCase: DeleteLayerFieldUseCase
) : ViewModel() {



    private val _uiState =
        MutableStateFlow(LayerFieldUiState())

    val uiState: StateFlow<LayerFieldUiState> =
        _uiState.asStateFlow()

    private var observeFieldsJob: Job? = null

    fun deleteField(
        fieldId: Long
    ) {

        viewModelScope.launch {

            try {

                deleteLayerFieldUseCase(
                    fieldId
                )

            } catch (e: Exception) {

                _uiState.update {

                    it.copy(
                        errorMessage =
                            e.message
                                ?: "Unable to delete field"
                    )
                }
            }
        }
    }

    fun openLayer(
        layer: Layer
    ) {

        observeFieldsJob?.cancel()

        _uiState.update {
            it.copy(
                selectedLayer = layer,
                fields = emptyList(),
                fieldName = "",
                selectedFieldType = FieldType.TEXT,
                isRequired = false,
                errorMessage = null
            )
        }

        observeFieldsJob =
            viewModelScope.launch {

                getFieldsForLayerUseCase(
                    layerId = layer.id
                ).collect {
                    fields ->

                    _uiState.update {
                        it.copy(
                            fields = fields
                        )
                    }
                }
            }
    }

    fun closeLayer() {

        // Stop observing only.
        observeFieldsJob?.cancel()
        observeFieldsJob = null

        // Keep selectedLayer and fields available
        // for feature capture.
        _uiState.update {
            it.copy(
                fieldName = "",
                selectedFieldType = FieldType.TEXT,
                isRequired = false,
                errorMessage = null,
                isCreatingField = false
            )
        }
    }

    fun onFieldNameChange(
        name: String
    ) {

        _uiState.update {
            it.copy(
                fieldName = name,
                errorMessage = null
            )
        }
    }

    fun onFieldTypeChange(
        fieldType: FieldType
    ) {

        _uiState.update {
            it.copy(
                selectedFieldType = fieldType
            )
        }
    }

    fun onRequiredChange(
        required: Boolean
    ) {

        _uiState.update {
            it.copy(
                isRequired = required
            )
        }
    }

    fun createField() {

        val state = _uiState.value

        val layer =
            state.selectedLayer ?: return

        val cleanName =
            state.fieldName.trim()

        if (cleanName.isEmpty()) {

            _uiState.update {
                it.copy(
                    errorMessage =
                        "Enter field name"
                )
            }

            return
        }

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isCreatingField = true,
                    errorMessage = null
                )
            }

            try {

                createLayerFieldUseCase(
                    layerId = layer.id,
                    name = cleanName,
                    fieldType =
                        state.selectedFieldType,
                    isRequired =
                        state.isRequired
                )

                _uiState.update {
                    it.copy(
                        fieldName = "",
                        selectedFieldType =
                            FieldType.TEXT,
                        isRequired = false,
                        isCreatingField = false
                    )
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isCreatingField = false,
                        errorMessage =
                            e.message
                                ?: "Unable to create field"
                    )
                }
            }
        }
    }
}