package com.example.myapplication.ui.surfarea

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
import com.example.myapplication.model.conditions.ConditionStatus
import com.example.myapplication.model.metalerts.Alert
import com.example.myapplication.model.smacklip.DayForecast
import com.example.myapplication.model.surfareas.SurfArea
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class DailySurfAreaScreenUiState(
    val location: SurfArea? = null,
    val alerts: List<Alert> = emptyList(),
    val wavePeriods: List<Double?> = emptyList(), // .size == in 18..21

    val conditionStatuses: List<Map<LocalDateTime, ConditionStatus>> = emptyList(),
    val forecast7Days: List<DayForecast> = emptyList(),
    val loading: Boolean = false
)

class DailySurfAreaScreenViewModel: ViewModel() {
    private val smackLipRepository = SmackLipRepositoryImpl()
    private val _dailySurfAreaScreenUiState = MutableStateFlow(DailySurfAreaScreenUiState())
    val dailySurfAreaScreenUiState: StateFlow<DailySurfAreaScreenUiState> = _dailySurfAreaScreenUiState.asStateFlow()

    fun updateStatusConditions(surfArea: SurfArea, forecast: List<DayForecast>) {
        viewModelScope.launch(Dispatchers.IO) {

            _dailySurfAreaScreenUiState.update {state ->
                Log.d("DSVM", "Updating statuses")
                val newConditionStatuses: MutableList<Map<LocalDateTime, ConditionStatus>> = mutableListOf()
                if (forecast.isEmpty()) {
                    Log.d("DSVM", "Forecast empty, quitting update")
                    return@launch
                }

                forecast.map {dayForecast ->
                    val todaysStatuses: MutableMap<LocalDateTime, ConditionStatus> = mutableMapOf()

                    dayForecast.data.entries.map {(time, dataAtTime) ->
                        val wavePeriod = try{state.wavePeriods[forecast.indexOf(dayForecast) * time.hour]}
                        catch (e: IndexOutOfBoundsException) {null}

                        val conditionStatus = smackLipRepository.getConditionStatus(
                            location = surfArea,
                            wavePeriod = wavePeriod,
                            windSpeed = dataAtTime.windSpeed,
                            windGust = dataAtTime.windGust,
                            windDir = dataAtTime.windDir,
                            waveHeight = dataAtTime.waveHeight,
                            waveDir = dataAtTime.waveDir,
                        )
                        todaysStatuses[time] = conditionStatus
                    }
                    newConditionStatuses.add(todaysStatuses.toMap())
                }

                state.copy(
                    conditionStatuses = newConditionStatuses.toList()
                )
            }
            _dailySurfAreaScreenUiState.update {
                it.copy(loading = false) //avslutte loading screen - det siste som kalles fra DailySurfScreen
            }
        }
    }


    fun updateOFLFNext7Days(surfArea: SurfArea)  {
        viewModelScope.launch(Dispatchers.IO) {
            //starte loading screen - det første som kalles fra DailySurfScreen
            _dailySurfAreaScreenUiState.update {
                if (it.forecast7Days.isNotEmpty()) {
                    Log.d("DSVM", "Quiiting update of OFLF, data already loaded")
                    return@launch
                }
                if (surfArea == it.location) {
                    Log.d("DSVM", "Data already updated for $surfArea")
                    return@launch
                }
                it.copy(loading = true, location = surfArea)
            }
            _dailySurfAreaScreenUiState.update {state ->
                val newForecast7Days: List<DayForecast> = smackLipRepository.getSurfAreaOFLFNext7Days(surfArea).forecast
                state.copy(forecast7Days = newForecast7Days)
            }

        }
    }


    // TODO: Get from repo stateflow
    fun updateWavePeriods(surfArea: SurfArea){
        viewModelScope.launch(Dispatchers.IO) {

            _dailySurfAreaScreenUiState.update {
                if (surfArea == it.location && it.wavePeriods.isNotEmpty()) {
                    Log.d("DSVM", "Waveperiods already updated for $surfArea")
                    return@launch
                }
                val newWavePeriods = smackLipRepository.getWavePeriodsNext3DaysForArea(surfArea)
                it.copy(wavePeriods = newWavePeriods)
            }

        }
    }



}