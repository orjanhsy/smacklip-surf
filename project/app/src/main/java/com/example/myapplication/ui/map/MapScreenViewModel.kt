package com.example.myapplication.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.map.MapRepositoryImpl
import com.example.myapplication.model.surfareas.SurfArea
import com.mapbox.geojson.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MapScreenUiState(
    val points: List<Pair<SurfArea, Point>> = emptyList() //bruker ikke denne, men bytter den ut når vi trenger viewmodel for andre deler av mapscreen

)


class MapScreenViewModel : ViewModel() {


    private val _mapScreenUiState = MutableStateFlow(MapScreenUiState())
    val mapScreenUiState: StateFlow<MapScreenUiState> = _mapScreenUiState.asStateFlow()





}
