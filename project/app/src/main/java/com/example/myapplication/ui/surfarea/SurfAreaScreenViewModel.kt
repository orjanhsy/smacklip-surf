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
    val waveHeights : List<Pair<List<Int>, Double>> = emptyList(),
    val windDirections : List<Pair<List<Int>, Double>> = emptyList(),
    val windSpeeds : List<Pair<List<Int>, Double>> = emptyList(),
    val windSpeedOfGusts : List<Pair<List<Int>, Double>> = emptyList(),
    val next24Hours : List<List<Pair<List<Int>, Double>>> = emptyList()
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

    //TODO: kan være vi ikke trenger alle de enkelte update-metodene?


    fun getForecastNext24Hours(){
        viewModelScope.launch(Dispatchers.IO) {
            _surfAreaScreenUiState.update {

                val newNext24Hours : MutableList<List<Pair<List<Int>, Double>>> = mutableListOf()

                val waveHeight = smackLipRepository.getWaveHeights()
                val windDirection = smackLipRepository.getWindDirection()
                val windSpeed = smackLipRepository.getWindSpeed()
                val windSpeedOfGust = smackLipRepository.getWindSpeedOfGust()

                for (i in 0 until 24) {
                    newNext24Hours.add(listOf(
                        waveHeight[i],
                        windDirection[i],
                        windSpeed[i],
                        windSpeedOfGust[i]))
                }
                it.copy(next24Hours = newNext24Hours)

            }
        }
    }
}

