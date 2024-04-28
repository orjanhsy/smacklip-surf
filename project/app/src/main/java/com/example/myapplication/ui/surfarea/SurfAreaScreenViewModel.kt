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
    val waveHeights: List<List<Pair<List<Int>, Double>>> = emptyList(),
    val waveDirections: List<List<Pair<List<Int>, Double>>> = emptyList(),
    val wavePeriods: List<Double?> = emptyList(),
    val maxWaveHeights: List<Double> = emptyList(),
    val minWaveHeights: List<Double> = emptyList(),
    val windDirections: List<List<Pair<List<Int>, Double>>> = emptyList(),
    val windSpeeds: List<List<Pair<List<Int>, Double>>> = emptyList(),
    val windSpeedOfGusts: List<List<Pair<List<Int>, Double>>> = emptyList(),
    //val forecast7Days: MutableList<List<Pair<List<Int>, List<Any>>>> = mutableListOf(),
    val conditionStatuses: Map<Int, List<ConditionStatus>> = mutableMapOf(),
    val bestConditionStatuses: Map<Int, ConditionStatus> = mutableMapOf(),

    val forecastNext7Days: List<Map<List<Int>, List<Any>>> = mutableListOf() //hører til den nye metoden med async

)



class SurfAreaScreenViewModel: ViewModel() {
    private val smackLipRepository = SmackLipRepositoryImpl()
    private val _surfAreaScreenUiState = MutableStateFlow(SurfAreaScreenUiState())
    val surfAreaScreenUiState: StateFlow<SurfAreaScreenUiState> = _surfAreaScreenUiState.asStateFlow()

    init {
    }

    fun asyncNext7Days(surfArea: SurfArea){
        viewModelScope.launch(Dispatchers.IO) {
            val newNext7Days = smackLipRepository.getSurfAreaOFLFNext7Days(surfArea)
            val newMaxWaveHeights = newNext7Days.map {it.maxBy { entry -> entry.value[5] as Double }.value[5] as Double}
            val newMinWaveHeights = newNext7Days.map {it.minBy { entry -> entry.value[5] as Double }.value[5] as Double}
            _surfAreaScreenUiState.update {
                it.copy (
                    forecastNext7Days = newNext7Days,
                    maxWaveHeights = newMaxWaveHeights,
                    minWaveHeights = newMinWaveHeights
                )
            }
        }
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

//    fun updateMaxWaveHeights() {
//        viewModelScope.launch {
//            _surfAreaScreenUiState.update {state ->
//                state.copy(
//                    maxWaveHeights = state.waveHeights.map {
//                        day -> day.maxBy {hour -> hour.second as Double}
//                    }.map {it.second}
//                )
//            }
//        }
//    }

    fun updateWavePeriods(surfArea: SurfArea) {
        viewModelScope.launch(Dispatchers.IO) {
            _surfAreaScreenUiState.update {state ->
                val newWavePeriods = smackLipRepository.getWavePeriodsNext3DaysForArea(surfArea)
                state.copy(
                    wavePeriods = newWavePeriods
                )
            }
        }
    }

    fun updateConditionStatuses(surfArea: SurfArea, forecast7Days: MutableList<List<Pair<List<Int>, List<Any>>>>) {
        viewModelScope.launch(Dispatchers.IO) {
            _surfAreaScreenUiState.update { state ->
                var newConditionStatuses: MutableMap<Int, MutableList<ConditionStatus>> = mutableMapOf()
                var newBestConditionStatuses: MutableMap< Int, ConditionStatus> = mutableMapOf()

                for (day in 0.. 2) {
                    newConditionStatuses[day] = mutableListOf()
                    if (forecast7Days.isEmpty()) {
                        Log.d("SAVM", "Attempted to update condition status on empty forecast7Days")
                        return@launch
                    }
                    for (hour in 0..<forecast7Days[day].size) {
                        val wavePeriod = try {state.wavePeriods[(day+1)*hour]} catch (e: IndexOutOfBoundsException) {null}
                        newConditionStatuses[day]!!.add (
                            smackLipRepository.getConditionStatus(
                                surfArea,
                                wavePeriod,
                                state.waveHeights[day][hour].second,
                                state.waveDirections[day][hour].second,
                                state.windDirections[day][hour].second,
                                state.windSpeeds[day][hour].second,
                                state.windSpeedOfGusts[day][hour].second,
                                state.alerts
                            )
                        )
                    }
                    if (ConditionStatus.GREAT in newConditionStatuses[day]!!) {
                        newBestConditionStatuses[day] = ConditionStatus.GREAT
                    } else if (ConditionStatus.DECENT in newConditionStatuses[day]!!) {
                        newBestConditionStatuses[day] = ConditionStatus.DECENT
                    } else if (ConditionStatus.POOR in newConditionStatuses[day]!!) {
                        newBestConditionStatuses[day] = ConditionStatus.POOR
                    } else {
                        newBestConditionStatuses[day] = ConditionStatus.BLANK
                    }
                }
                Log.d("SAVM", "Updating conditionStatuses with ${newConditionStatuses.filter { it.value.all {xd -> xd != ConditionStatus.BLANK }}.size}")

                state.copy(
                    conditionStatuses = newConditionStatuses,
                    bestConditionStatuses =  newBestConditionStatuses
                )
            }
        }
    }

}

