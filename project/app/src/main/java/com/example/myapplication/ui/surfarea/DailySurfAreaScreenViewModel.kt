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
)

class DailySurfAreaScreenViewModel(
    private val forecastRepo: Repository
): ViewModel() {

    val dailySurfAreaScreenUiState: StateFlow<DailySurfAreaScreenUiState> = combine(
        forecastRepo.ofLfNext7Days,
        forecastRepo.wavePeriods,
        forecastRepo.areaInFocus,
        forecastRepo.dayInFocus
    ) { oflf, wavePeriods, sa, day ->
        val today = LocalDateTime.now().dayOfMonth

        // gets oflf data from area at day
        val newDataAtDay: DayForecast = oflf.next7Days[sa]?.forecast?.get(day?.minus(today) ?: 0) ?: DayForecast()

        // get wavePeriods for area at day
        val newWavePeriods: List<Double?> = wavePeriods.wavePeriods[sa]?.get(day) ?: listOf()

        Log.d("DSVM", "Updated waveperiods with $newWavePeriods for $sa at $day")

        // gets conditions for each forecast interval for area at day
        val times = newDataAtDay.data.map {it.key}.sortedBy { it.hour }
        val conditionUtil = ConditionUtils()
        val newConditionStatuses = newDataAtDay.data.mapValues {(time, dataAtTime) ->
            try {
                val conditionStatus = conditionUtil.getConditionStatus(
                    location = sa,
                    wavePeriod = newWavePeriods[times.indexOf(time)],
                    windSpeed = dataAtTime.windSpeed,
                    windDir =  dataAtTime.windDir,
                    waveHeight = dataAtTime.waveHeight,
                    waveDir = dataAtTime.waveDir,
                )
                Log.d("DSVM", "Conditions: $dataAtTime and tp: ${newWavePeriods[times.indexOf(time)]} resulted in $conditionStatus for $sa")
                conditionStatus
            } catch (e: IndexOutOfBoundsException) {
                // handles situations where wavePeriods are not forecast
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
            forecastRepo.updateDayInFocus(day)
        }
    }

}