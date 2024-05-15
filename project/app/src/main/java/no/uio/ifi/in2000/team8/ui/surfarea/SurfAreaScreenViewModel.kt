package com.example.myapplication.ui.surfarea

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.metalerts.MetAlertsRepository
import com.example.myapplication.data.weatherforecast.WeatherForecastRepository
import com.example.myapplication.model.conditions.ConditionStatus
import com.example.myapplication.model.metalerts.Alert
import com.example.myapplication.model.weatherforecast.Forecast7DaysOFLF
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.utils.ConditionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SurfAreaScreenUiState(
    val forecastNext7Days: Forecast7DaysOFLF = Forecast7DaysOFLF(),
    val alertsSurfArea: List<Alert> = emptyList(),
    val maxWaveHeights: List<Double> = emptyList(),
    val minWaveHeights: List<Double> = emptyList(),
    val bestConditionStatusPerDay: List<ConditionStatus> = mutableListOf(),
)



class SurfAreaScreenViewModel(
    private val forecastRepo: WeatherForecastRepository,
    private val alertsRepo: MetAlertsRepository
): ViewModel() {

    val surfAreaScreenUiState: StateFlow<SurfAreaScreenUiState> = combine(
        forecastRepo.ofLfForecast,
        forecastRepo.wavePeriods,
        forecastRepo.areaInFocus,
        alertsRepo.alerts
    ) { oflf, wavePeriods, sa, alerts ->

        // gets forecast data and alerts relevant to certain surf area
        val newOfLf: Forecast7DaysOFLF = oflf.next7Days[sa] ?: Forecast7DaysOFLF()
        val newAlerts: List<Alert> = alerts[sa] ?: listOf()
        val wavePeriodsInArea: Map<Int, List<Double?>>  = wavePeriods.wavePeriods[sa] ?: mapOf()

        // gets min-max waveHeights per day for display
        val newMaxWaveHeights = newOfLf.forecast.map {
            it.data.values.maxOf {dataAtTime -> dataAtTime.waveHeight }
        }
        val newMinWaveHeights = newOfLf.forecast.map {
            it.data.values.minOf {dataAtTime -> dataAtTime.waveHeight }
        }

        // creates list of the best conditions per day
        val conditionUtil = ConditionUtils()
        val newBestConditions = newOfLf.forecast.map {dayForecasts ->
            val availableTimes = dayForecasts.data.keys.sortedBy {time -> time.hour }

            dayForecasts.data.entries.map { (time, dataAtTime) ->
                val conditionStatus = try {
                    conditionUtil.getConditionStatus(
                        location = sa,
                        wavePeriod = wavePeriodsInArea[time.dayOfMonth]?.get(availableTimes.indexOf(time)),
                        windSpeed = dataAtTime.windSpeed,
                        windDir = dataAtTime.windDir,
                        waveHeight = dataAtTime.waveHeight,
                        waveDir = dataAtTime.waveDir
                    )

                } catch (e: IndexOutOfBoundsException) {
                    // handles situations where wave periods are no longer forecast
                    ConditionStatus.BLANK
                }
                conditionStatus
            }.minBy {status ->  status.value }// filters out the best condition of the day for display
        }

        SurfAreaScreenUiState(
            forecastNext7Days = newOfLf,
            alertsSurfArea = newAlerts,
            maxWaveHeights = newMaxWaveHeights,
            minWaveHeights = newMinWaveHeights,
            bestConditionStatusPerDay = newBestConditions
        )

    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        SurfAreaScreenUiState()
    )

    fun updateLocation(surfArea: SurfArea) {
        viewModelScope.launch(Dispatchers.IO){
            forecastRepo.updateAreaInFocus(surfArea)
        }
    }


}


