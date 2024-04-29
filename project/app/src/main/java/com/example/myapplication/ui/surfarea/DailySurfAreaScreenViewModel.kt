package com.example.myapplication.ui.surfarea

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
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy

data class DailySurfAreaScreenUiState(
    val location: SurfArea? = null,
    val alerts: List<Features> = emptyList(),
    val waveHeights: List<Pair<List<Int>, Double>> = emptyList(),
    val waveDirections: List<Pair<List<Int>, Double>> = emptyList(),
    val wavePeriods: List<Double?> = emptyList(), // .size == in 18..21
    val windDirections: List<Pair<List<Int>, Double>> = emptyList(),
    val windSpeeds: List<Pair<List<Int>, Double>> = emptyList(),
    val windSpeedOfGusts: List<Pair<List<Int>, Double>> = emptyList(),

    val conditionStatuses: List<Map<List<Int>, ConditionStatus>> = emptyList(),
    val forecast7Days: List<Map<List<Int>, List<Any>>> = emptyList(),
)

class DailySurfAreaScreenViewModel: ViewModel() {
    private val smackLipRepository = SmackLipRepositoryImpl()
    private val _dailySurfAreaScreenUiState = MutableStateFlow(DailySurfAreaScreenUiState())
    val dailySurfAreaScreenUiState: StateFlow<DailySurfAreaScreenUiState> = _dailySurfAreaScreenUiState.asStateFlow()


    /*

    fun updateWaveHeights(surfArea: SurfArea) {
        viewModelScope.launch(Dispatchers.IO) {
            _dailySurfAreaScreenUiState.update {
                val newWaveHeights = smackLipRepository.getWaveHeights(surfArea)
                it.copy(waveHeights = newWaveHeights)
            }
        }
    }
    */
    /*
    fun updateWaveDirections(surfArea: SurfArea) {
        viewModelScope.launch(Dispatchers.IO) {
            _dailySurfAreaScreenUiState.update {
                val newWaveDirections = smackLipRepository.getWaveDirections(surfArea)
                it.copy(waveDirections = newWaveDirections)
            }
        }
    }*/

    /*
    fun updateWindDirection(surfArea: SurfArea) {
        viewModelScope.launch(Dispatchers.IO) {
            _dailySurfAreaScreenUiState.update {
                val newWindDirection = smackLipRepository.getWindDirection(surfArea)
                it.copy(windDirections = newWindDirection)
            }
        }
    }*/

    /*
    fun updateWindSpeed(surfArea: SurfArea) {
        viewModelScope.launch(Dispatchers.IO) {
            _dailySurfAreaScreenUiState.update {
                val newWindSpeed = smackLipRepository.getWindSpeed(surfArea)
                it.copy(windSpeeds = newWindSpeed)
            }
        }
    }*/

    /*
    fun updateWindSpeedOfGust(surfArea: SurfArea) {
        viewModelScope.launch(Dispatchers.IO) {
            _dailySurfAreaScreenUiState.update {
                val newWindSpeedOfGust = smackLipRepository.getWindSpeedOfGust(surfArea)
                it.copy(windSpeedOfGusts = newWindSpeedOfGust)
            }
        }
    }
    */


    // TODO: test usage
    fun updateStatusConditions(surfArea: SurfArea, forecast: List<Map<List<Int>, List<Any>>>) {
        viewModelScope.launch(Dispatchers.IO) {
            _dailySurfAreaScreenUiState.update {state ->
                val newConditionStatuses: MutableList<Map<List<Int>, ConditionStatus>> = mutableListOf()
                if (forecast.isEmpty()) {
                    return@launch
                }

                forecast.map {dayMap ->
                    val todaysStatuses: MutableMap<List<Int>, ConditionStatus> = mutableMapOf()
                    dayMap.entries.map {(time, data) ->
                        val conditionStatus = smackLipRepository.getConditionStatus(
                            location = surfArea,
                            wavePeriod = state.wavePeriods[0], // TODO: change to logical wavePeriod
                            windSpeed = data[0] as Double,
                            windGust = data[1] as Double,
                            windDir = data[2] as Double,
                            waveHeight = data[5] as Double,
                            waveDir = data[6] as Double,
                            alerts = state.alerts
                        )
                        todaysStatuses[time] = conditionStatus
                    }
                    newConditionStatuses.add(todaysStatuses.toMap())
                }

                state.copy(
                    conditionStatuses = newConditionStatuses.toList()
                )
            }
        }
    }


    fun updateOFLFNext7Days(surfArea: SurfArea)  {
        viewModelScope.launch(Dispatchers.IO) {
            _dailySurfAreaScreenUiState.update {state ->
                val newForecast7Days: List<Map<List<Int>, List<Any>>> = smackLipRepository.getSurfAreaOFLFNext7Days(surfArea)
                state.copy(forecast7Days = newForecast7Days)
            }
        }
    }




    fun updateWavePeriods(surfArea: SurfArea){
        viewModelScope.launch(Dispatchers.IO) {
            _dailySurfAreaScreenUiState.update {
                val newWavePeriods = smackLipRepository.getWavePeriodsNext3DaysForArea(surfArea)
                it.copy(wavePeriods = newWavePeriods)
            }
        }
    }



}