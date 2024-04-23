package com.example.myapplication.ui.surfarea

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
import com.example.myapplication.model.conditions.ConditionStatus
import com.example.myapplication.model.metalerts.Features
import com.example.myapplication.model.surfareas.SurfArea
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.IndexOutOfBoundsException

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
    val forecast7Days: MutableList<List<Pair<List<Int>, List<Any>>>> = mutableListOf(),
    val conditionStatuses: Map<Int, List<ConditionStatus>> = mutableMapOf(),
    val bestConditionStatuses: Map<Int, ConditionStatus> = mutableMapOf()

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

    fun updateWavePeriods(surfArea: SurfArea) {
        viewModelScope.launch {
            _surfAreaScreenUiState.update {state ->
                val newWavePeriods = smackLipRepository.getWavePeriodsNext3DaysForArea(surfArea)
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

    fun updateConditionStatuses(surfArea: SurfArea, forecast7Days: MutableList<List<Pair<List<Int>, List<Any>>>>) {
        viewModelScope.launch(Dispatchers.IO) {
            _surfAreaScreenUiState.update { state ->
                var newConditionStatuses: MutableMap<Int, MutableList<ConditionStatus>> = mutableMapOf()

                for (day in 0.. 2) {
                    newConditionStatuses[day] = mutableListOf()
                    if (forecast7Days.isEmpty()) {
                        return@launch
                    }
                    for (hour in 0 .. forecast7Days[day].size) {
                        val wavePeriod = try {state.wavePeriods[(day+1)*hour]} catch (e: IndexOutOfBoundsException) {null}
                        newConditionStatuses[day]!!.add (
                            smackLipRepository.getConditionStatus(
                                surfArea,
                                wavePeriod,
                                state.waveHeights[day][hour].second as Double,
                                state.waveDirections[day][hour].second as Double,
                                state.windDirections[day][hour].second as Double,
                                state.windSpeeds[day][hour].second as Double,
                                state.windSpeedOfGusts[day][hour].second as Double,
                                state.alerts
                            )
                        )
                    }
                }
                state.copy(
                    conditionStatuses = newConditionStatuses
                )
            }
        }
    }

}

