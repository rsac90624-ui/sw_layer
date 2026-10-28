package com.example.sw_layer_application.feature.map.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sw_layer_application.domain.model.FieldType
import com.example.sw_layer_application.feature.map.LayerFieldUiState


@Composable
fun ManageFieldsPanel(
    uiState: LayerFieldUiState,
    onFieldNameChange: (String) -> Unit,
    onFieldTypeChange: (FieldType) -> Unit,
    onRequiredChange: (Boolean) -> Unit,
    onCreateField: () -> Unit,
    onDeleteField: (Long) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),

        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Manage Fields",
            style = MaterialTheme.typography.titleLarge
        )


        uiState.selectedLayer?.let { layer ->

            Text(
                text =
                    "Layer: ${layer.name} (${layer.geometryType.name})"
            )
        }


        HorizontalDivider()


        Text(
            text = "Existing Fields"
        )


        if (uiState.fields.isEmpty()) {

            Text(
                text = "No fields created"
            )

        } else {

            uiState.fields.forEach { field ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column {

                        Text(
                            text =
                                if(field.isRequired)
                                    "${field.name} *"
                                else
                                    field.name
                        )

                        Text(
                            text = field.fieldType.name
                        )
                    }


                    TextButton(
                        onClick = {

                            onDeleteField(
                                field.id
                            )

                        }
                    ) {

                        Text(
                            text = "Delete"
                        )
                    }
                }
            }
        }


        HorizontalDivider()


        Text(
            text = "Add Field"
        )


        OutlinedTextField(

            value = uiState.fieldName,

            onValueChange =
                onFieldNameChange,

            label = {
                Text("Field Name")
            },

            modifier =
                Modifier.fillMaxWidth(),

            singleLine = true
        )


        Text(
            text = "Field Type"
        )


        FieldTypeOption(
            title = "Text",
            selected =
                uiState.selectedFieldType == FieldType.TEXT,
            onClick = {
                onFieldTypeChange(
                    FieldType.TEXT
                )
            }
        )


        FieldTypeOption(
            title = "Integer",
            selected =
                uiState.selectedFieldType == FieldType.INTEGER,
            onClick = {
                onFieldTypeChange(
                    FieldType.INTEGER
                )
            }
        )


        FieldTypeOption(
            title = "Decimal",
            selected =
                uiState.selectedFieldType == FieldType.DECIMAL,
            onClick = {
                onFieldTypeChange(
                    FieldType.DECIMAL
                )
            }
        )


        FieldTypeOption(
            title = "Boolean",
            selected =
                uiState.selectedFieldType == FieldType.BOOLEAN,
            onClick = {
                onFieldTypeChange(
                    FieldType.BOOLEAN
                )
            }
        )


        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = uiState.isRequired,
                onCheckedChange =
                    onRequiredChange
            )

            Text(
                text = "Required Field"
            )
        }


        uiState.errorMessage?.let {

            Text(
                text = it
            )
        }


        Button(

            onClick = onCreateField,

            modifier =
                Modifier.fillMaxWidth(),

            enabled =
                uiState.fieldName.isNotBlank() &&
                        !uiState.isCreatingField
        ) {

            Text(
                text =
                    if(uiState.isCreatingField)
                        "Adding..."
                    else
                        "Add Field"
            )
        }
    }
}



@Composable
private fun FieldTypeOption(
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