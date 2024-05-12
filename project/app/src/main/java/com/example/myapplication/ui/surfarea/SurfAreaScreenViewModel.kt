package com.example.myapplication.ui.surfarea

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.smackLip.Repository
import com.example.myapplication.model.conditions.ConditionStatus
import com.example.myapplication.model.metalerts.Alert
import com.example.myapplication.model.smacklip.Forecast7DaysOFLF
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
    val wavePeriods: Map<Int, List<Double?>> = emptyMap(),
    val maxWaveHeights: List<Double> = emptyList(),
    val minWaveHeights: List<Double> = emptyList(),
    val bestConditionStatusPerDay: List<ConditionStatus> = mutableListOf(),
)



class SurfAreaScreenViewModel(
    private val repo: Repository
): ViewModel() {

    val surfAreaScreenUiState: StateFlow<SurfAreaScreenUiState> = combine(
        repo.ofLfNext7Days,
        repo.alerts,
        repo.wavePeriods,
        repo.areaInFocus
    ) { oflf, alerts, wavePeriods, sa ->
        // TODO: !! ?
        val newOfLf: Forecast7DaysOFLF = oflf.next7Days[sa] ?: Forecast7DaysOFLF()
        val newAlerts: List<Alert> = alerts[sa] ?: listOf()
        val newWavePeriods: Map<Int, List<Double?>>  = wavePeriods.wavePeriods[sa] ?: mapOf()
        val conditionUtil = ConditionUtils()

        val newMaxWaveHeights = newOfLf.forecast.map {
            it.data.values.maxOf {dataAtTime -> dataAtTime.waveHeight }
        }
        val newMinWaveHeights = newOfLf.forecast.map {
            it.data.values.minOf {dataAtTime -> dataAtTime.waveHeight }
        }

        val newBestConditions = try {
            newOfLf.forecast.map {dayForecasts ->
                val availableTimes = dayForecasts.data.keys.sortedBy {time -> time.hour }

                dayForecasts.data.entries.map { (time, dataAtTime) ->
                    val conditionStatus = try {
                        conditionUtil.getConditionStatus(
                            location = sa,
                            wavePeriod = newWavePeriods[time.dayOfMonth]?.get(availableTimes.indexOf(time)),
                            windSpeed = dataAtTime.windSpeed,
                            windDir = dataAtTime.windDir,
                            waveHeight = dataAtTime.waveHeight,
                            waveDir = dataAtTime.waveDir
                        )

                    } catch (e: IndexOutOfBoundsException) {
                        ConditionStatus.BLANK
                    }
                    conditionStatus
                }.minBy {status ->  status.value }
            }
        } catch (e: Exception) {
            Log.d("SAVM", "Statuses not updated because ${e.message}")
            listOf()
        }

        SurfAreaScreenUiState(
            forecastNext7Days = newOfLf,
            alertsSurfArea = newAlerts,
            wavePeriods = newWavePeriods,
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
            repo.updateAreaInFocus(surfArea)
        }
    }


}


