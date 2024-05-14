package com.example.myapplication.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.weatherforecast.WeatherForecastRepository
import com.example.myapplication.model.weatherforecast.DataAtTime
import com.example.myapplication.model.surfareas.SurfArea
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class MapScreenUiState(
    val oflfNow : Map<SurfArea, DataAtTime> = emptyMap()
)


class MapScreenViewModel(
    private val weatherForecastRepository: WeatherForecastRepository
) : ViewModel() {

    //add data from weatherForecastRepository to oflfnow
    val mapScreenUiState: StateFlow<MapScreenUiState> =
        weatherForecastRepository.ofLfForecast.map{ oflf->
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
