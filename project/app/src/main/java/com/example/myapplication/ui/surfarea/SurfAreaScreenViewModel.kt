package com.example.myapplication.ui.surfarea

import android.util.Log
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

data class SurfAreaScreenUiState(
    val location: SurfArea? = null,
    val alerts: List<Features> = emptyList(),
    // .size=7 for the following:
    val waveHeights: List<List<Pair<List<Int>, Any>>> = emptyList(),
    val waveDirections: List<List<Pair<List<Int>, Any>>> = emptyList(),
    val wavePeriods: List<Double?> = emptyList(),
    val maxWaveHeights: List<Any> = emptyList(),
    val windDirections: List<List<Pair<List<Int>, Any>>> = emptyList(),
    val windSpeeds: List<List<Pair<List<Int>, Any>>> = emptyList(),
    val windSpeedOfGusts: List<List<Pair<List<Int>, Any>>> = emptyList(),
    val forecast7Days: MutableList<List<Pair<List<Int>, List<Any>>>> = mutableListOf()

    )



class SurfAreaScreenViewModel: ViewModel() {
    private val smackLipRepository = SmackLipRepositoryImpl()
    private val _surfAreaScreenUiState = MutableStateFlow(SurfAreaScreenUiState())
    val surfAreaScreenUiState: StateFlow<SurfAreaScreenUiState> = _surfAreaScreenUiState.asStateFlow()

    init {
    }

    fun updateLocation(surfArea: SurfArea) {
        viewModelScope.launch(Dispatchers.IO) {
            _surfAreaScreenUiState.update {
                it.copy (location = surfArea)
            }
        }
    }
    fun updateAlerts() {
        viewModelScope.launch(Dispatchers.IO) {
            _surfAreaScreenUiState.update {
                val newAlerts = if (it.location != null) smackLipRepository.getRelevantAlertsFor(it.location) else listOf()
                it.copy(alerts = newAlerts)
            }
        }
    }

    fun updateMaxWaveHeights() {
        viewModelScope.launch {
            _surfAreaScreenUiState.update {state ->
                state.copy(
                    maxWaveHeights = state.waveHeights.map {
                        day -> day.maxBy {hour -> hour.second as Double}
                    }.map {it.second}
                )
            }
        }
    }

    fun updateWavePeriods() {
        viewModelScope.launch {
            _surfAreaScreenUiState.update {state ->
                val newWavePeriods = if (state.location != null) {
                    smackLipRepository.getWavePeriodsNext3DaysForArea(state.location)
                } else { emptyList() }
                state.copy(
                    wavePeriods = newWavePeriods
                )
            }
        }
    }

    fun updateForecastNext7Days(surfArea: SurfArea){
        viewModelScope.launch(Dispatchers.IO) {
            _surfAreaScreenUiState.update {state ->
                val newForecast7Days = smackLipRepository.getDataForTheNext7Days(surfArea)
                Log.d("SAVM", "Updating forcast of vm by dat containing ${newForecast7Days.size} elements")
                val newWaveHeights = newForecast7Days.map { dayForecast ->  dayForecast.map { dayData -> dayData.first to dayData.second[0]}}
                val newMaxWaveHeights = newWaveHeights.map {day -> day.maxBy {hour -> hour.second as Double}}.map {it.second}
                val newWaveDirections = newForecast7Days.map { dayForecast ->  dayForecast.map { dayData -> dayData.first to dayData.second[1]}}
                val newWindDirections = newForecast7Days.map { dayForecast ->  dayForecast.map { dayData -> dayData.first to dayData.second[2]}}
                val newWindSpeeds = newForecast7Days.map { dayForecast ->  dayForecast.map { dayData -> dayData.first to dayData.second[3]}}
                val newWindSpeedOfGusts = newForecast7Days.map { dayForecast ->  dayForecast.map { dayData -> dayData.first to dayData.second[4]}}


                assert(newForecast7Days.isNotEmpty())
                Log.d("SAVM", "Updating waveheight with ${newMaxWaveHeights.size} elements")
                Log.d("SAVM", "Updating maxwaveheight with ${newMaxWaveHeights.size} elements")
                Log.d("SAVM", "Updating winddir with ${newMaxWaveHeights.size} elements")
                Log.d("SAVM", "Updating windspeed with ${newMaxWaveHeights.size} elements")
                Log.d("SAVM", "Updating windgust with ${newMaxWaveHeights.size} elements")
                state.copy(
                    location = surfArea,
                    forecast7Days = newForecast7Days,
                    waveHeights = newWaveHeights,
                    waveDirections = newWaveDirections,
                    maxWaveHeights = newMaxWaveHeights,
                    windDirections = newWindDirections,
                    windSpeeds = newWindSpeeds,
                    windSpeedOfGusts = newWindSpeedOfGusts
                )
            }
        }
    }

    fun getConditionStatus(
        location: SurfArea,
        windSpeed: Double,
        windGust: Double,
        windDir: Double,
        waveHeight: Double,
        waveDir: Double,
        wavePeriod: Double,
        alerts: List<Features>
    ): String {
        return smackLipRepository.getConditionStatus(location, windSpeed, windGust, windDir, waveHeight, waveDir, wavePeriod, alerts)
    }

}

