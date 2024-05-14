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
    val oflfNow : Map<SurfArea, DataAtTime> = emptyMap()
)


class MapScreenViewModel(
    private val weatherForecastRepository: WeatherForecastRepository
) : ViewModel() {

    //add data from weatherForecastRepository to oflfnow
    val mapScreenUiState: StateFlow<MapScreenUiState> =
        weatherForecastRepository.ofLfNext7Days.map{ oflf->
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
