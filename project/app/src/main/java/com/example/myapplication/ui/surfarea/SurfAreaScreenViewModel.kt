package com.example.myapplication.ui.surfarea

import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
import com.example.myapplication.model.surfareas.SurfArea
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
    val forecast7Days: MutableList<List<Pair<List<Int>, List<Double>>>> = mutableListOf()
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

    fun updateWindDirection(surfArea: SurfArea) {
        viewModelScope.launch(Dispatchers.IO) {
            _surfAreaScreenUiState.update {
                val newWindDirection = smackLipRepository.getWindDirection(surfArea)
                it.copy(windDirections = newWindDirection)
            }
        }
    }

    fun updateWindSpeed(surfArea: SurfArea) {
        viewModelScope.launch(Dispatchers.IO) {
            _surfAreaScreenUiState.update {
                val newWindSpeed = smackLipRepository.getWindSpeed(surfArea)
                it.copy(windSpeeds = newWindSpeed)
            }
        }
    }

    fun updateWindSpeedOfGust(surfArea: SurfArea) {
        viewModelScope.launch(Dispatchers.IO) {
            _surfAreaScreenUiState.update {
                val newWindSpeedOfGust = smackLipRepository.getWindSpeedOfGust(surfArea)
                it.copy(windSpeedOfGusts = newWindSpeedOfGust)
            }
        }
    }


    fun getForecastNext7Days(surfArea: SurfArea){
        viewModelScope.launch(Dispatchers.IO) {
            _surfAreaScreenUiState.update {
                val newForecast7Days = smackLipRepository.getDataForTheNext7Days(surfArea)
                it.copy(forecast7Days = newForecast7Days)

            }
        }
    }
}

