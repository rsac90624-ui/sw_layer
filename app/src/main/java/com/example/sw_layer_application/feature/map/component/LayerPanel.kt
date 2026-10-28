package com.example.sw_layer_application.feature.map.component


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sw_layer_application.domain.model.GeometryType
import com.example.sw_layer_application.domain.model.Layer
import com.example.sw_layer_application.feature.map.LayerUiState

@Composable
fun LayerPanel(
    uiState: LayerUiState,
    onLayerNameChange: (String) -> Unit,
    onGeometryTypeChange: (GeometryType) -> Unit,
    onCreateLayer: () -> Unit,
    onLayerClick: (Layer) -> Unit

) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Layers"
        )

        HorizontalDivider()

        if (uiState.layers.isEmpty()) {

            Text(
                text = "No layers created"
            )

        } else {

            uiState.layers.forEach { layer ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onLayerClick(layer)
                        }
                        .padding(vertical = 12.dp),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    Text(
                        text = layer.name
                    )

                    Text(
                        text = layer.geometryType.name
                    )
                }
            }
        }

        HorizontalDivider()

        Text(
            text = "Add Layer"
        )

        OutlinedTextField(
            value = uiState.layerName,
            onValueChange = onLayerNameChange,
            label = {
                Text("Layer Name")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isCreatingLayer
        )

        Text(
            text = "Geometry Type"
        )

        GeometryOption(
            title = "Point",
            selected =
                uiState.selectedGeometryType ==
                        GeometryType.POINT,
            onClick = {
                onGeometryTypeChange(
                    GeometryType.POINT
                )
            }
        )

        GeometryOption(
            title = "Line",
            selected =
                uiState.selectedGeometryType ==
                        GeometryType.LINE,
            onClick = {
                onGeometryTypeChange(
                    GeometryType.LINE
                )
            }
        )

        GeometryOption(
            title = "Polygon",
            selected =
                uiState.selectedGeometryType ==
                        GeometryType.POLYGON,
            onClick = {
                onGeometryTypeChange(
                    GeometryType.POLYGON
                )
            }
        )

        uiState.errorMessage?.let { message ->

            Text(
                text = message
            )
        }

        Button(
            onClick = onCreateLayer,
            modifier = Modifier.fillMaxWidth(),
            enabled =
                uiState.layerName.isNotBlank() &&
                        !uiState.isCreatingLayer
        ) {

            Text(
                text =
                    if (uiState.isCreatingLayer) {
                        "Creating..."
                    } else {
                        "Create Layer"
                    }
            )
        }
    }
}

@Composable
private fun GeometryOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Row(
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Text(
            text = title
        )
    }
}