package com.example.myapplication.ui.surfarea

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.smackLip.Repository
import com.example.myapplication.data.smackLip.RepositoryImpl
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
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

    val conditionStatuses: List<Map<LocalDateTime, ConditionStatus>> = emptyList(),
    val forecast7Days: DayForecast = DayForecast(),
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
        val newForecast7DaysOFLF: DayForecast = try { oflf.next7Days[sa]!!.forecast[day!!] }
        catch(e: NullPointerException) {DayForecast()}

        val newWavePeriods: List<Double?> = try { wavePeriods.wavePeriods[sa]!![day]!! }
        catch(e: IndexOutOfBoundsException) {listOf()}

        DailySurfAreaScreenUiState(
            forecast7Days = newForecast7DaysOFLF,
            wavePeriods = newWavePeriods,
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