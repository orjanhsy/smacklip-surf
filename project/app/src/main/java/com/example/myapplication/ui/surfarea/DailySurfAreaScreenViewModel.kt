package com.example.myapplication.ui.surfarea

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.smackLip.Repository
import com.example.myapplication.model.conditions.ConditionStatus
import com.example.myapplication.model.metalerts.Alert
import com.example.myapplication.model.smacklip.DayForecast
import com.example.myapplication.utils.ConditionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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
        val conditionUtil = ConditionUtils()
        val today = LocalDateTime.now().dayOfMonth
        val newDataAtDay: DayForecast = oflf.next7Days[sa]?.forecast?.get(day?.minus(today) ?: 0) ?: DayForecast()

        val newWavePeriods: List<Double?> = wavePeriods.wavePeriods[sa]?.get(day) ?: listOf()

        Log.d("DSVM", "Updated waveperiods with $newWavePeriods for $sa at $day")

        val times = newDataAtDay.data.map {it.key}.sortedBy { it.hour }
        val newConditionStatuses = newDataAtDay.data.mapValues {(time, dataAtTime) ->
            try {
                val conditionStatus = conditionUtil.getConditionStatus(
                    location = sa!!,
                    wavePeriod = newWavePeriods[times.indexOf(time)],
                    windSpeed = dataAtTime.windSpeed,
                    windDir =  dataAtTime.windDir,
                    waveHeight = dataAtTime.waveHeight,
                    waveDir = dataAtTime.waveDir,
                )
                Log.d("DSVM", "Conditions: $dataAtTime and tp: ${newWavePeriods[times.indexOf(time)]} resulted in $conditionStatus for $sa")
                conditionStatus
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

}