package com.example.sw_layer_application.domain.usecase


import com.example.sw_layer_application.domain.model.Feature
import com.example.sw_layer_application.domain.repository.FeatureRepository
import javax.inject.Inject

class CreateFeatureUseCase @Inject constructor(
    private val repository: FeatureRepository
) {

    suspend operator fun invoke(
        feature: Feature
    ): Long {

        return repository.createFeature(
            feature = feature
        )
    }
}