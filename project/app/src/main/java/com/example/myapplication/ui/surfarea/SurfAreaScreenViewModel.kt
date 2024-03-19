package com.example.myapplication.ui.surfarea

import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
import com.example.myapplication.model.SurfArea
import com.example.myapplication.model.metalerts.Features
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SurfAreaScreenUiState(
    val location: SurfArea? = null,
    val alerts: List<Features> = emptyList(),
    val waveHeights: List<Pair<List<Int>, Double>> = emptyList(),
    val windDirections: List<Pair<List<Int>, Double>> = emptyList(),
    val windSpeeds: List<Pair<List<Int>, Double>> = emptyList(),
    val windSpeedOfGusts: List<Pair<List<Int>, Double>> = emptyList(),
    val forecast3Days24Hours: MutableList<MutableList<Pair<List<Int>, Pair<Int, List<Double>>>>> = mutableListOf()
    )



class SurfAreaScreenViewModel: ViewModel() {
    private val smackLipRepository = SmackLipRepositoryImpl()
    private val _surfAreaScreenUiState = MutableStateFlow(SurfAreaScreenUiState())
    val surfAreaScreenUiState: StateFlow<SurfAreaScreenUiState> = _surfAreaScreenUiState.asStateFlow()


    fun updateAlerts() {
        viewModelScope.launch(Dispatchers.IO) {
            _surfAreaScreenUiState.update {
                val newAlerts = if (it.location != null) smackLipRepository.getRelevantAlertsFor(it.location) else listOf()
                it.copy(alerts = newAlerts)
            }
        }
    }

    fun updateWaveHeights() {
        viewModelScope.launch(Dispatchers.IO) {
            _surfAreaScreenUiState.update {
                val newWaveHeights = smackLipRepository.getWaveHeights()
                it.copy(waveHeights = newWaveHeights)
            }
        }
    }

    fun updateWindDirection() {
        viewModelScope.launch(Dispatchers.IO) {
            _surfAreaScreenUiState.update {
                val newWindDirection = smackLipRepository.getWindDirection()
                it.copy(windDirections = newWindDirection)
            }
        }
    }

    fun updateWindSpeed() {
        viewModelScope.launch(Dispatchers.IO) {
            _surfAreaScreenUiState.update {
                val newWindSpeed = smackLipRepository.getWindSpeed()
                it.copy(windSpeeds = newWindSpeed)
            }
        }
    }

    fun updateWindSpeedOfGust() {
        viewModelScope.launch(Dispatchers.IO) {
            _surfAreaScreenUiState.update {
                val newWindSpeedOfGust = smackLipRepository.getWindSpeedOfGust()
                it.copy(windSpeedOfGusts = newWindSpeedOfGust)
            }
        }
    }

    //ble litt usikker på om denne skulle ta inn en dag, hvis metoden i homescreen tar inn en dag
    //bør denne også det. Må i såfall kanskje ha flere dager i UiStaten
    fun getForecast3Days24Hours(){
        viewModelScope.launch(Dispatchers.IO) {
            _surfAreaScreenUiState.update {
                val newForecast3Days24Hours = smackLipRepository.getForecastNext24Hours()
                it.copy(forecast3Days24Hours = newForecast3Days24Hours)

            }
        }
    }
}

