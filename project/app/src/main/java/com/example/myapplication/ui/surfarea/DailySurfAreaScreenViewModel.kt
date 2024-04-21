package com.example.myapplication.ui.surfarea

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
import com.example.myapplication.model.metalerts.Features
import com.example.myapplication.model.surfareas.SurfArea
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DailySurfAreaScreenUiState(
    val location: SurfArea? = null,
    val alerts: List<Features> = emptyList(),
    val waveHeights: List<Pair<List<Int>, Double>> = emptyList(),
    val waveDirections: List<Pair<List<Int>, Double>> = emptyList(),
    val wavePeriods: List<Double?> = emptyList(), // .size == in 18..21
    val windDirections: List<Pair<List<Int>, Double>> = emptyList(),
    val windSpeeds: List<Pair<List<Int>, Double>> = emptyList(),
    val windSpeedOfGusts: List<Pair<List<Int>, Double>> = emptyList(),
    val forecast7Days: MutableList<List<Pair<List<Int>, List<Any>>>> = mutableListOf(),
)

class DailySurfAreaScreenViewModel: ViewModel() {
    private val smackLipRepository = SmackLipRepositoryImpl()
    private val _dailySurfAreaScreenUiState = MutableStateFlow(DailySurfAreaScreenUiState())
    val dailySurfAreaScreenUiState: StateFlow<DailySurfAreaScreenUiState> = _dailySurfAreaScreenUiState.asStateFlow()



    fun updateWaveHeights(surfArea: SurfArea) {
        viewModelScope.launch(Dispatchers.IO) {
            _dailySurfAreaScreenUiState.update {
                val newWaveHeights = smackLipRepository.getWaveHeights(surfArea)
                it.copy(waveHeights = newWaveHeights)
            }
        }
    }
    fun updateWaveDirections(surfArea: SurfArea) {
        viewModelScope.launch(Dispatchers.IO) {
            _dailySurfAreaScreenUiState.update {
                val newWaveDirections = smackLipRepository.getWaveDirections(surfArea)
                it.copy(waveDirections = newWaveDirections)
            }
        }
    }

    fun updateWindDirection(surfArea: SurfArea) {
        viewModelScope.launch(Dispatchers.IO) {
            _dailySurfAreaScreenUiState.update {
                val newWindDirection = smackLipRepository.getWindDirection(surfArea)
                it.copy(windDirections = newWindDirection)
            }
        }
    }

    fun updateWindSpeed(surfArea: SurfArea) {
        viewModelScope.launch(Dispatchers.IO) {
            _dailySurfAreaScreenUiState.update {
                val newWindSpeed = smackLipRepository.getWindSpeed(surfArea)
                it.copy(windSpeeds = newWindSpeed)
            }
        }
    }

    fun updateWindSpeedOfGust(surfArea: SurfArea) {
        viewModelScope.launch(Dispatchers.IO) {
            _dailySurfAreaScreenUiState.update {
                val newWindSpeedOfGust = smackLipRepository.getWindSpeedOfGust(surfArea)
                it.copy(windSpeedOfGusts = newWindSpeedOfGust)
            }
        }
    }


    fun updateForecastNext7Days(surfArea: SurfArea)  {
        viewModelScope.launch(Dispatchers.IO) {
            _dailySurfAreaScreenUiState.update {
                val newForecast7Days = smackLipRepository.getDataForTheNext7Days(surfArea)
                it.copy(forecast7Days = newForecast7Days)

            }
        }
    }



}