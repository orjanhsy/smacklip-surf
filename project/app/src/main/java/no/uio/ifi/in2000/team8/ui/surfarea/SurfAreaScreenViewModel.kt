package no.uio.ifi.in2000.team8.ui.surfarea

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import no.uio.ifi.in2000.team8.data.metalerts.MetAlertsRepository
import no.uio.ifi.in2000.team8.data.weatherforecast.WeatherForecastRepository
import no.uio.ifi.in2000.team8.model.conditions.ConditionStatus
import no.uio.ifi.in2000.team8.model.metalerts.Alert
import no.uio.ifi.in2000.team8.model.weatherforecast.Forecast7DaysOFLF
import no.uio.ifi.in2000.team8.model.surfareas.SurfArea
import no.uio.ifi.in2000.team8.utils.ConditionUtils
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
        val newOfLf: Forecast7DaysOFLF = oflf.forecasts[sa] ?: Forecast7DaysOFLF()
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


