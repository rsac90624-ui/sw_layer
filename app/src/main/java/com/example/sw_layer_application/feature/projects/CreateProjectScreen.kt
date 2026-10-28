package com.example.sw_layer_application.feature.projects



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CreateProjectScreen(
    onProjectCreated: () -> Unit,
    viewModel: ProjectViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.projectCreated) {

        if (uiState.projectCreated) {
            onProjectCreated()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Create Project"
        )

        OutlinedTextField(
            value = uiState.projectName,
            onValueChange = viewModel::onProjectNameChange,
            label = {
                Text("Project Name")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isCreating
        )

        uiState.errorMessage?.let { message ->

            Text(
                text = message
            )
        }

        Button(
            onClick = viewModel::createProject,
            enabled =
                uiState.projectName.isNotBlank() &&
                        !uiState.isCreating,
            modifier = Modifier.fillMaxWidth()
        ) {

            if (uiState.isCreating) {

                CircularProgressIndicator()

            } else {

                Text("Create Project")
            }
        }
    }
}