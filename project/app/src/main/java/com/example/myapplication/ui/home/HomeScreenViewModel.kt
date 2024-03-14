package com.example.myapplication.ui.home

import com.example.myapplication.model.metalerts.Features

data class HomeScreenUiState(
    val name : String,
    val windSpeed : Double,
    val windGust : Double,
    val windDirection : String,
    val waveHeight : List<Pair<String, Double>>,
    val relevantAlerts : List<Features>
)
class HomeScreenViewModel(

) {

}