package no.uio.ifi.in2000.team8.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import no.uio.ifi.in2000.team8.data.weatherforecast.WeatherForecastRepository
import no.uio.ifi.in2000.team8.model.weatherforecast.DataAtTime
import no.uio.ifi.in2000.team8.model.surfareas.SurfArea
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
        val ofLfNow: Map<SurfArea, DataAtTime> = ofLf.forecasts.entries.associate {
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
