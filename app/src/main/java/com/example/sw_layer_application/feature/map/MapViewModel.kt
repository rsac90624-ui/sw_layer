package com.example.sw_layer_application.feature.map

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sw_layer_application.core.location.LocationProvider
import com.example.sw_layer_application.domain.usecase.GetProjectsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getProjectsUseCase: GetProjectsUseCase,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())

    val uiState: StateFlow<MapUiState> =
        _uiState.asStateFlow()

    init {
        observeProjects()
    }

    private fun observeProjects() {

        viewModelScope.launch {

            getProjectsUseCase().collect { projects ->

                _uiState.update {
                    it.copy(
                        projects = projects,
                        activeProject = projects.firstOrNull(),
                        isLoading = false
                    )
                }
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun loadCurrentLocation() {

        if (!locationProvider.hasLocationPermission()) {

            _uiState.update {
                it.copy(
                    locationError = "Location permission not granted"
                )
            }

            return
        }

        val location =
            locationProvider.getLastKnownLocation()

        if (location != null) {

            _uiState.update {
                it.copy(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    locationError = null
                )
            }

        } else {

            _uiState.update {
                it.copy(
                    locationError = "Current location not available"
                )
            }
        }
    }
}