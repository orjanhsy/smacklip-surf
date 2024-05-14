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
    val ofLfNow : Map<SurfArea, DataAtTime> = emptyMap()
)


class MapScreenViewModel(
    weatherForecastRepository: WeatherForecastRepository
) : ViewModel() {

    //add data from weatherForecastRepository to ofLfNow
    val mapScreenUiState: StateFlow<MapScreenUiState> =
        weatherForecastRepository.ofLfForecast.map{ ofLf ->
        val ofLfNow: Map<SurfArea, DataAtTime> = ofLf.next7Days.entries.associate {
            it.key to it.value.forecast[0].data.entries.sortedBy {timeToData -> timeToData.key.hour }[0].value
        }

        MapScreenUiState(
            ofLfNow = ofLfNow
        )

    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        MapScreenUiState()
    )

}
