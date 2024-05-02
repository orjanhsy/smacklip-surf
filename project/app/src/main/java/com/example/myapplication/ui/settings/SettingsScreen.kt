package com.example.myapplication.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.NavigationManager
import com.example.myapplication.ui.common.composables.BottomBar
import com.example.myapplication.ui.theme.AppTheme


@OptIn(ExperimentalMaterial3Api::class)
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(settingsScreenViewmodel: SettingsScreenViewModel = viewModel()) {
    val settingsUiState : SettingsUiState by settingsScreenViewmodel.settingsUiState.collectAsState()
    val navController = NavigationManager.navController

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(text = "Settings")
                })
        },
        bottomBar = {
            BottomBar(
                onNavigateToHomeScreen = {
                    navController?.navigate("HomeScreen")
                    // Navigerer til HomeScreen
                },
                onNavigateToMapScreen = {
                    navController?.navigate("MapScreen")
                    // Navigerer til HomeScreen
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            val currentState = settingsUiState
            when (currentState) {
                is SettingsUiState.Loading -> {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .padding(innerPadding)
                        )
                    }
                }
                is SettingsUiState.Loaded -> {
                    item {
                        DarkModeCard(
                            darkModeEnabled = currentState.settings.darkMode,
                            onDarkModeToggle = { enabled ->
                                settingsScreenViewmodel.setDarkMode(enabled)

                            }
                        )
                    }
                    item {
                        TestValueCard(
                            testValue = currentState.settings.test,
                            onTestValueChanged = { value ->
                                settingsScreenViewmodel.setTest(value)

                            }

                        )
                    }
                    item {
                        InfoCardSettings()
                    }
                }
                is SettingsUiState.Error -> {
                    item {
                        Text(
                            text = "Error: ${currentState.message}"
                        )
                    }
                }
            }
        }

    }
}
@Composable
fun DarkModeCard(
    darkModeEnabled: Boolean,
    onDarkModeToggle: (Boolean) -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
                .clickable { onDarkModeToggle(!darkModeEnabled) }
        ) {
            Switch(
                checked = darkModeEnabled,
                onCheckedChange = null,
                modifier = Modifier.padding(end = 16.dp)
            )
            Text(text = "Dark Mode")

        }

    }

}
@Composable
fun TestValueCard(
    testValue: Double,
    onTestValueChanged: (Double) -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Test Value",
            )
            OutlinedTextField(
                value = testValue.toString(),
                onValueChange = { onTestValueChanged(it.toDoubleOrNull() ?: 0.0) },
                label = { Text("Enter test value") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
@Composable
fun InfoCardSettings(){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Info",
            )
            Text(
                text = "Info her...",
            )
        }
    }
}




@Preview(showBackground = true)
@Composable
private fun PreviewSettingsScreen(){
    AppTheme {
        SettingsScreen()
    }
}



