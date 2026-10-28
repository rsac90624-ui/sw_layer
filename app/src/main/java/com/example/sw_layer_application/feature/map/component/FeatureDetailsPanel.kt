package com.example.sw_layer_application.feature.map.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sw_layer_application.domain.model.Feature
import org.json.JSONObject

@Composable
fun FeatureDetailsPanel(
    feature: Feature,
    onClose: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),

        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Feature Details",
            style = MaterialTheme.typography.titleLarge
        )

        HorizontalDivider()

        Text(
            text = "Feature ID: ${feature.id}"
        )

        Text(
            text = "Layer ID: ${feature.layerId}"
        )

        Text(
            text = "Geometry: ${feature.geometryType}"
        )

        feature.latitude?.let {
            Text(
                text = "Latitude: %.6f".format(it)
            )
        }

        feature.longitude?.let {
            Text(
                text = "Longitude: %.6f".format(it)
            )
        }

        HorizontalDivider()

        Text(
            text = "Attributes",
            style = MaterialTheme.typography.titleMedium
        )

        val attributes =
            runCatching {
                JSONObject(
                    feature.attributesJson ?: "{}"
                )
            }.getOrNull()

        if (
            attributes == null ||
            attributes.length() == 0
        ) {

            Text(
                text = "No attributes"
            )

        } else {

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                val keys = attributes.keys()

                while (keys.hasNext()) {

                    val key = keys.next()

                    Text(
                        text =
                            "$key: ${attributes.opt(key)}"
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            OutlinedButton(
                onClick = onClose,
                modifier = Modifier.weight(1f)
            ) {
                Text("Close")
            }

            Button(
                onClick = onEdit,
                modifier = Modifier.weight(1f)
            ) {
                Text("Edit")
            }

            OutlinedButton(
                onClick = onDelete,
                modifier = Modifier.weight(1f)
            ) {
                Text("Delete")
            }
        }
    }
}