package com.example.sw_layer_application.feature.map.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sw_layer_application.domain.model.LayerField
import com.example.sw_layer_application.domain.model.FieldType

@Composable
fun FeatureAttributePanel(
    fields: List<LayerField>,
    values: Map<String, String>,
    onValueChange: (
        fieldName: String,
        value: String
    ) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),

        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Feature Attributes",
            style =
                MaterialTheme.typography.titleLarge
        )

        HorizontalDivider()

        if (fields.isEmpty()) {

            Text(
                text = "No attributes defined for this layer."
            )

        } else {

            fields.forEach { field ->

                FeatureAttributeField(
                    field = field,

                    value =
                        values[field.name]
                            ?: "",

                    onValueChange = { value ->

                        onValueChange(
                            field.name,
                            value
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun FeatureAttributeField(
    field: LayerField,
    value: String,
    onValueChange: (String) -> Unit
) {

    val fieldType = field.fieldType

    when (fieldType) {

        FieldType.TEXT -> {

            OutlinedTextField(
                value = value,

                onValueChange =
                    onValueChange,

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text(
                        text =
                            if (field.isRequired)
                                "${field.name} *"
                            else
                                field.name
                    )
                },

                singleLine = true
            )
        }

        FieldType.INTEGER -> {

            // existing code
        }

        FieldType.DECIMAL -> {

            // existing code
        }

        FieldType.BOOLEAN -> {

            // existing code
        }


    }
}