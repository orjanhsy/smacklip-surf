package com.example.myapplication.ui.map

import android.health.connect.datatypes.units.Temperature
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.map.MapRepositoryImpl
import com.example.myapplication.data.smackLip.Repository
import com.example.myapplication.model.metalerts.Alert
import com.example.myapplication.model.smacklip.DataAtTime
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.ui.home.HomeScreenUiState
import com.mapbox.geojson.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    private val repo: Repository
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
