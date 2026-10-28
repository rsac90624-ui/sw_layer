package com.example.sw_layer_application.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sw_layer_application.domain.model.Feature
import com.example.sw_layer_application.domain.repository.FeatureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class FeatureViewModel @Inject constructor(
    private val featureRepository: FeatureRepository
) : ViewModel() {



    private val _features =
        MutableStateFlow<List<Feature>>(emptyList())

    private val _attributeUiState =
        MutableStateFlow(
            FeatureAttributeUiState()
        )

    private val _selectedFeature =
        MutableStateFlow<Feature?>(null)

    val selectedFeature: StateFlow<Feature?> =
        _selectedFeature.asStateFlow()

    val attributeUiState: StateFlow<FeatureAttributeUiState> =
        _attributeUiState.asStateFlow()

    val features: StateFlow<List<Feature>> =
        _features.asStateFlow()

    fun selectFeature(featureId: Long) {

        viewModelScope.launch {

            val feature =
                featureRepository.getFeatureById(featureId)

            _selectedFeature.value = feature

            if (feature != null) {

                val values =
                    runCatching {

                        val json =
                            JSONObject(
                                feature.attributesJson ?: "{}"
                            )

                        buildMap<String, String> {

                            val keys = json.keys()

                            while (keys.hasNext()) {

                                val key = keys.next()

                                put(
                                    key,
                                    json.opt(key)?.toString() ?: ""
                                )
                            }
                        }

                    }.getOrDefault(emptyMap())

                _attributeUiState.update {
                    it.copy(
                        values = values,
                        errorMessage = null
                    )
                }
            }
        }
    }

    fun clearSelectedFeature() {
        _selectedFeature.value = null
    }

    fun loadFeaturesForLayer(layerId: Long) {

        viewModelScope.launch {

            featureRepository
                .getFeaturesForLayer(layerId)
                .collect { featureList ->

                    _features.value = featureList
                }
        }
    }

    fun attributesToJson(): String {

        val jsonObject = JSONObject()

        _attributeUiState.value.values.forEach { (key, value) ->

            jsonObject.put(
                key,
                value
            )
        }

        return jsonObject.toString()
    }

    fun clearFeatures() {
        _features.value = emptyList()
    }

    fun createFeature(
        feature: Feature,
        onSuccess: (Long) -> Unit,
        onError: (Throwable) -> Unit
    ) {

        viewModelScope.launch {

            try {

                val featureId =
                    featureRepository.createFeature(feature)

                onSuccess(featureId)

            } catch (e: Throwable) {

                onError(e)
            }
        }
    }
    fun updateFeature(
        feature: Feature,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {

        viewModelScope.launch {

            try {

                featureRepository.updateFeature(
                    feature
                )

                onSuccess()

            } catch (e: Throwable) {

                onError(e)
            }
        }
    }

    fun updateFeatureAttributes(
        featureId: Long,
        attributesJson: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {

                val updated =
                    featureRepository.updateFeatureAttributes(
                        featureId = featureId,
                        attributesJson = attributesJson
                    )

                if (!updated) {
                    throw IllegalStateException(
                        "Feature was not updated"
                    )
                }

                onSuccess()

            } catch (e: Throwable) {
                onError(e)
            }
        }
    }

    fun deleteFeature(
        featureId: Long,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {

                featureRepository.deleteFeature(
                    featureId
                )

                onSuccess()

            } catch (e: Throwable) {

                onError(e)
            }
        }
    }

    fun loadAttributeValues(attributesJson: String?) {

        val values =
            runCatching {

                val json =
                    JSONObject(
                        attributesJson ?: "{}"
                    )

                buildMap<String, String> {

                    val keys = json.keys()

                    while (keys.hasNext()) {

                        val key = keys.next()

                        put(
                            key,
                            json.opt(key)?.toString() ?: ""
                        )
                    }
                }

            }.getOrDefault(emptyMap())

        _attributeUiState.update { state ->

            state.copy(
                values = values,
                errorMessage = null
            )
        }
    }





    fun onAttributeValueChange(
        fieldName: String,
        value: String
    ) {

        _attributeUiState.update { state ->

            state.copy(
                values =
                    state.values.toMutableMap().apply {
                        put(
                            fieldName,
                            value
                        )
                    },
                errorMessage = null
            )
        }
    }

    fun clearAttributeValues() {

        _attributeUiState.value =
            FeatureAttributeUiState()
    }
}