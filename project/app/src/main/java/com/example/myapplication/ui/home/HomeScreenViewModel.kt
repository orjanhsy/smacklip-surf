package com.example.myapplication.ui.home

import com.example.myapplication.model.metalerts.Features

data class HomeScreenUiState(
    val name : String,
    val windSpeed : List<Pair<String, Double>>,
    val windGust : List<Pair<String, Double>>,
    val windDirection : List<Pair<String, Double>>,
    val waveHeight : List<Pair<String, Double>>,
    val relevantAlerts : List<Features>
)
class HomeScreenViewModel(

) {

}