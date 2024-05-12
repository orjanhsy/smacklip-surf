package com.example.myapplication.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.weatherForecast.WeatherForecastRepository
import com.example.myapplication.model.smacklip.DataAtTime
import com.example.myapplication.model.surfareas.SurfArea
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class MapScreenUiState(
    /*
    val windSpeed: Map<SurfArea, List<Pair<List<Int>, Double>>> = emptyMap(),
    val windGust: Map<SurfArea, List<Pair<List<Int>, Double>>> = emptyMap(),
    val airTemperature: Map<SurfArea,List<Pair<List<Int>, Double>>> = emptyMap(),
    val symbolCode: Map<SurfArea,List<Pair<List<Int>, String>>> = emptyMap(),
    val waveHeight: Map<SurfArea, List<Pair<List<Int>, Double>>> = emptyMap(),
     */
    val oflfNow : Map<SurfArea, DataAtTime> = emptyMap()
)


class MapScreenViewModel(
    private val repo: WeatherForecastRepository
) : ViewModel() {

    val mapScreenUiState: StateFlow<MapScreenUiState> =
        repo.ofLfNext7Days.map{ oflf->
        val oflfNow: Map<SurfArea, DataAtTime> = oflf.next7Days.entries.associate {
            it.key to it.value.forecast[0].data.entries.sortedBy {timeToData -> timeToData.key.hour }[0].value
        }

        MapScreenUiState(
            oflfNow = oflfNow
        )

    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        MapScreenUiState()
    )

}
