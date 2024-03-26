package com.example.myapplication.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.map.mapRepositoryImpl
import com.example.myapplication.model.SurfArea
import com.mapbox.geojson.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MapScreenUiState(
    val points: List<Pair<SurfArea, Point>> = emptyList()
)


class MapScreenViewModel : ViewModel() {

    private val mapRepository = mapRepositoryImpl()
    private val _mapScreenUiState = MutableStateFlow(MapScreenUiState())
    val mapScreenUiState: StateFlow<MapScreenUiState> = _mapScreenUiState.asStateFlow()

    init {
        getPoints()
    }

   fun getPoints() {
        viewModelScope.launch(Dispatchers.IO) {
            _mapScreenUiState.update {
                val getPoints = mapRepository.locationToPoint()
                it.copy(points = getPoints)
            }
        }
    }
}