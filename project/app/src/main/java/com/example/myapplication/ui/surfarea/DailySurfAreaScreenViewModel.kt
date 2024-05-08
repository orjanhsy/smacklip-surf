package com.example.myapplication.ui.surfarea

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.smackLip.Repository
import com.example.myapplication.data.smackLip.RepositoryImpl
import com.example.myapplication.domain.GetConditionStatusUseCase
import com.example.myapplication.model.conditions.ConditionStatus
import com.example.myapplication.model.metalerts.Alert
import com.example.myapplication.model.smacklip.DayForecast
import com.example.myapplication.model.surfareas.SurfArea
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class DailySurfAreaScreenUiState(
    val alerts: List<Alert> = emptyList(),
    val wavePeriods: List<Double?> = emptyList(),

    val conditionStatuses: Map<LocalDateTime, ConditionStatus> = emptyMap(),
    val dataAtDay: DayForecast = DayForecast(),
    val loading: Boolean = false
)

class DailySurfAreaScreenViewModel(
    val repo: Repository
): ViewModel() {

    val dailySurfAreaScreenUiState: StateFlow<DailySurfAreaScreenUiState> = combine(
        repo.ofLfNext7Days,
        repo.wavePeriods,
        repo.areaInFocus,
        repo.dayInFocus
    ) { oflf, wavePeriods, sa, day ->
        //TODO: !!
        val today = LocalDateTime.now().dayOfMonth
        val newDataAtDay: DayForecast = try { oflf.next7Days[sa]!!.forecast[day!! - today] }
        catch(e: NullPointerException) {DayForecast()}

        val newWavePeriods: List<Double?> = try { wavePeriods.wavePeriods[sa]!![day]!! }
        catch(e: NullPointerException) {listOf()}
//        Log.d("DSVM", "Updated waveperiods with $newWavePeriods for $sa at $day")

        val times = newDataAtDay.data.map {it.key}.sortedBy { it.hour }
        val newConditionStatuses = newDataAtDay.data.mapValues {(time, dataAtTime) ->
            try {
                val conditionStatus =
                    GetConditionStatusUseCase(sa!!, dataAtTime, newWavePeriods[times.indexOf(time)])
                val cs = conditionStatus()
                Log.d("DSVM", "Conditions: $dataAtTime and tp: ${newWavePeriods[times.indexOf(time)]} resulted in $cs for $sa")
                cs
            } catch (e: IndexOutOfBoundsException) {
                ConditionStatus.BLANK
            }
        }

        DailySurfAreaScreenUiState(
            dataAtDay = newDataAtDay,
            wavePeriods = newWavePeriods,
            conditionStatuses = newConditionStatuses
        )

    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        DailySurfAreaScreenUiState()
    )

    fun updateDayInFocus(day: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            repo.updateDayInFocus(day)
        }
    }


//    fun updateStatusConditions(surfArea: SurfArea, forecast: List<DayForecast>) {
//        viewModelScope.launch(Dispatchers.IO) {
//
//            _dailySurfAreaScreenUiState.update {state ->
//                Log.d("DSVM", "Updating statuses")
//                val newConditionStatuses: MutableList<Map<LocalDateTime, ConditionStatus>> = mutableListOf()
//                if (forecast.isEmpty()) {
//                    Log.d("DSVM", "Forecast empty, quitting update")
//                    return@launch
//                }
//
//                forecast.map {dayForecast ->
//                    val todaysStatuses: MutableMap<LocalDateTime, ConditionStatus> = mutableMapOf()
//
//                    dayForecast.data.entries.map {(time, dataAtTime) ->
//                        val wavePeriod = try{state.wavePeriods[forecast.indexOf(dayForecast) * time.hour]}
//                        catch (e: IndexOutOfBoundsException) {null}
//
//                        val conditionStatus = smackLipRepository.getConditionStatus(
//                            location = surfArea,
//                            wavePeriod = wavePeriod,
//                            windSpeed = dataAtTime.windSpeed,
//                            windGust = dataAtTime.windGust,
//                            windDir = dataAtTime.windDir,
//                            waveHeight = dataAtTime.waveHeight,
//                            waveDir = dataAtTime.waveDir,
//                        )
//                        todaysStatuses[time] = conditionStatus
//                    }
//                    newConditionStatuses.add(todaysStatuses.toMap())
//                }
//
//                state.copy(
//                    conditionStatuses = newConditionStatuses.toList()
//                )
//            }
//            _dailySurfAreaScreenUiState.update {
//                it.copy(loading = false) //avslutte loading screen - det siste som kalles fra DailySurfScreen
//            }
//        }
//    }

}