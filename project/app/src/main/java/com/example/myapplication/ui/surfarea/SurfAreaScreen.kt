package com.example.myapplication.ui.surfarea

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SurfAreaScreen(surfAreaScreenViewModel: SurfAreaScreenViewModel = viewModel()) {
    val surfAreaScreenUiState: SurfAreaScreenUiState by surfAreaScreenViewModel.surfAreaScreenUiState.collectAsState()

}


//forslag
@Composable
fun ShowForecastNext24hrs(day: String) {
    // vis 0-6, 6-12, 12-18, 18-24

}

@Composable
fun Next24HoursCard() {

}

@Composable
fun ShowForecastNext7Days() {
    val days = listOf<String>("Monday", "Tuesday")
    days.forEach { ShowForecastNext24hrs(day = it) }
}