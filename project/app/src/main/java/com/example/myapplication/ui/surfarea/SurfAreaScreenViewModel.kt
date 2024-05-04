package com.example.myapplication.ui.surfarea

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.smackLip.Repository
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
import com.example.myapplication.model.conditions.ConditionStatus
import com.example.myapplication.model.metalerts.Alert
import com.example.myapplication.model.smacklip.DayForecast
import com.example.myapplication.model.smacklip.Forecast7DaysOFLF
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.ui.home.HomeScreenUiState
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
import kotlin.text.Typography.times

data class SurfAreaScreenUiState(
    val forecastNext7Days: Forecast7DaysOFLF = Forecast7DaysOFLF(),
    val alertsSurfArea: List<Alert> = emptyList(),
    val wavePeriods: List<Double?> = emptyList(),
    val maxWaveHeights: List<Double> = emptyList(),
    val minWaveHeights: List<Double> = emptyList(),
    val bestConditionStatuses: Map<Int, ConditionStatus> = mutableMapOf(),
)



class SurfAreaScreenViewModel(
    repo: Repository
): ViewModel() {


    val surfAreaScreenUiState: StateFlow<SurfAreaScreenUiState> = combine(
        repo.ofLfNext7Days,
        repo.alerts,
        repo.wavePeriods,
        repo.areaInFocus
    ) { oflf, alerts, wavePeriods, sa ->
        // TODO: !!
        val newOfLf = oflf.next7Days[sa]!!
        val newAlerts = alerts[sa]!!
        val newWavePeriods = wavePeriods.wavePeriods[sa]!!
        val newMaxWaveHeights = newOfLf.forecast.map {
            it.data.values.maxOf {dataAtTime -> dataAtTime.waveHeight }
        }
        val newMinWaveHeights = newOfLf.forecast.map {
            it.data.values.minOf {dataAtTime -> dataAtTime.waveHeight }
        }

        SurfAreaScreenUiState(
            forecastNext7Days = newOfLf,
            alertsSurfArea = newAlerts,
            wavePeriods = newWavePeriods,
            maxWaveHeights = newMaxWaveHeights,
            minWaveHeights = newMinWaveHeights,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        SurfAreaScreenUiState()
    )


    // map<tidspunkt -> [windSpeed, windSpeedOfGust, windDirection, airTemperature, symbolCode, Waveheight, waveDirection]>
//    fun updateBestConditionStatuses(surfArea: SurfArea, forecast7Days: List<DayForecast>) {
//        viewModelScope.launch(Dispatchers.IO) {
//            _surfAreaScreenUiState.update {
//                if (forecast7Days.isEmpty() || it.wavePeriods.isEmpty()) {
//                    Log.e("SAVM", "Attempted to update condition status on empty forecast7Days")
//                    return@launch
//                }
//                it.copy(loading = true) //starter loading screen
//            }
//            _surfAreaScreenUiState.update { state ->
//
//                val newBestConditionStatuses: MutableMap<Int, ConditionStatus> = mutableMapOf()
//
//                for (dayIndex in 0.. 2) {
//
//                    val dayForecast: DayForecast = forecast7Days[dayIndex]
//                    val times = dayForecast.data.keys.sortedWith (
//                        compareBy<LocalDateTime> { it.month }.thenBy { it.dayOfMonth }
//                    )
//                    var bestToday = ConditionStatus.BLANK
//
//                    for (time in times) {
//                        val wavePeriod = try {
//                            state.wavePeriods[(dayIndex + 1) * time.hour]
//                        } catch (e: IndexOutOfBoundsException) {
//                            Log.d("SAVM", "No status given as wavePeriods were out of bounds for ${(dayIndex + 1)}")
//                            null
//                        }
//                        val statusToday = smackLipRepository.getConditionStatus(
//                            location = surfArea,
//                            wavePeriod = wavePeriod,
//                            windSpeed  = dayForecast.data[time]!!.windSpeed,
//                            windGust   = dayForecast.data[time]!!.windGust,
//                            windDir    = dayForecast.data[time]!!.windDir,
//                            waveHeight = dayForecast.data[time]!!.waveHeight,
//                            waveDir    = dayForecast.data[time]!!.waveDir,
//                        )
//
//                        if (statusToday == ConditionStatus.GREAT) {
//                            bestToday = statusToday
//                            break
//                        } else if (bestToday == ConditionStatus.DECENT) {
//                            continue
//                        } else {
//                            bestToday = statusToday
//                        }
//                    }
//                    newBestConditionStatuses[dayIndex] = bestToday
//                }
//
//                Log.d("SAVM", "Updating status conditions with ${newBestConditionStatuses.values}")
//                state.copy(
//                    bestConditionStatuses =  newBestConditionStatuses
//                )
//            }
//            _surfAreaScreenUiState.update {
//                it.copy(loading = false) //avslutter visning av loading screen
//            }
//        }
//    }
}


