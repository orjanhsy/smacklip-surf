package com.example.myapplication.ui.surfarea

import com.example.myapplication.model.metalerts.Features

data class SurfAreaScreenUiState(
    val locationName : String = "",
    val windSpeed : List<Pair<String, Double>> = emptyList(),
    val windGust : List<Pair<String, Double>> = emptyList(),
    val windDirection : List<Pair<String, Double>> = emptyList(),
    val waveHeight : List<Pair<String, Double>> = emptyList(),
    val allRelevantAlerts : List<List<Features>> = emptyList()
)
class SurfAreaScreenViewModel {
}