package com.example.myapplication.ui.surfarea

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
import com.example.myapplication.model.conditions.ConditionStatus
import com.example.myapplication.model.metalerts.Features
import com.example.myapplication.model.smacklip.Forecast7DaysOFLF
import com.example.myapplication.model.surfareas.SurfArea
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SurfAreaScreenUiState(
    val location: SurfArea? = null,
    val alertsSurfArea: List<Features> = emptyList(),
    val wavePeriods: List<Double?> = emptyList(),
    val maxWaveHeights: List<Double> = emptyList(),
    val minWaveHeights: List<Double> = emptyList(),
    val bestConditionStatuses: Map<Int, ConditionStatus> = mutableMapOf(),

    val forecastNext7Days: Forecast7DaysOFLF = Forecast7DaysOFLF(), //hører til den nye metoden med async
    val loading: Boolean = false

)



class SurfAreaScreenViewModel: ViewModel() {
    private val smackLipRepository = SmackLipRepositoryImpl()
    private val _surfAreaScreenUiState = MutableStateFlow(SurfAreaScreenUiState())
    val surfAreaScreenUiState: StateFlow<SurfAreaScreenUiState> = _surfAreaScreenUiState.asStateFlow()


    fun asyncNext7Days(surfArea: SurfArea){
        viewModelScope.launch(Dispatchers.IO) {
            _surfAreaScreenUiState.update {
                it.copy(loading = true)
            }
            val newNext7Days: Forecast7DaysOFLF = smackLipRepository.getSurfAreaOFLFNext7Days(surfArea)
            val newMaxWaveHeights: List<Double> = newNext7Days.forecast.map{ dayData ->
                dayData.data.maxBy {
                    it.value.waveHeight
                }.value.waveHeight
            }

            val newMinWaveHeights: List<Double> = newNext7Days.forecast.map{ dayData ->
                dayData.data.minBy {
                    it.value.waveHeight
                }.value.waveHeight
            }
            _surfAreaScreenUiState.update {
                it.copy (
                    forecastNext7Days = newNext7Days,
                    maxWaveHeights = newMaxWaveHeights,
                    minWaveHeights = newMinWaveHeights
                )
            }
            _surfAreaScreenUiState.update {
                it.copy(loading = false)
            }
        }
    }

    fun updateAlertsSurfArea(surfArea: SurfArea) {
        viewModelScope.launch(Dispatchers.IO) {
            _surfAreaScreenUiState.update {
                val newAlerts = if (surfArea != null) smackLipRepository.getRelevantAlertsFor(surfArea) else listOf()
                it.copy(alertsSurfArea = newAlerts)
            }

        }
    }

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


    // map<tidspunkt -> [windSpeed, windSpeedOfGust, windDirection, airTemperature, symbolCode, Waveheight, waveDirection]>
    fun updateBestConditionStatuses(surfArea: SurfArea, forecast7Days: List<Map<List<Int>, List<Any>>>) {
        viewModelScope.launch(Dispatchers.IO) {
            _surfAreaScreenUiState.update {
                it.copy(loading = true) //starter loading screen
            }
            _surfAreaScreenUiState.update { state ->
                val newBestConditionStatuses: MutableMap<Int, ConditionStatus> = mutableMapOf()

                for (dayIndex in 0.. 2) {
                    if (forecast7Days.isEmpty()) {
                        Log.e("SAVM", "Attempted to update condition status on empty forecast7Days")
                        return@launch
                    }
                    val dayForecast: Map<List<Int>, List<Any>> = forecast7Days[dayIndex]
                    val times = dayForecast.keys.sortedBy { it[3] }
                    var bestToday = ConditionStatus.BLANK

                    for (time in times) {
                        val wavePeriod = try {
                            state.wavePeriods[(dayIndex + 1) * time[3]]
                        } catch (e: IndexOutOfBoundsException) {
                            Log.d("SAVM", "No status given as wavePeriods were out of bounds for ${(dayIndex + 1)}")
                            null
                        }
                        val statusToday = smackLipRepository.getConditionStatus(
                            location = surfArea,
                            wavePeriod = wavePeriod,
                            windSpeed  = dayForecast[time]!![0] as Double,
                            windGust   = dayForecast[time]!![1] as Double,
                            windDir    = dayForecast[time]!![2] as Double,
                            waveHeight = dayForecast[time]!![5] as Double,
                            waveDir    = dayForecast[time]!![6] as Double,
                        )

                        if (statusToday == ConditionStatus.GREAT) {
                            bestToday = statusToday
                            break
                        } else if (bestToday == ConditionStatus.DECENT) {
                            continue
                        } else {
                            bestToday = statusToday
                        }
                    }
                    newBestConditionStatuses[dayIndex] = bestToday
                }

                Log.d("SAVM", "Updating status conditions with ${newBestConditionStatuses.values}")
                state.copy(
                    bestConditionStatuses =  newBestConditionStatuses
                )
            }
            _surfAreaScreenUiState.update {
                it.copy(loading = false) //avslutter visning av loading screen
            }
        }
    }
}


