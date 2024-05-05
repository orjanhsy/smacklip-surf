package com.example.myapplication.ui.map

import android.health.connect.datatypes.units.Temperature
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.map.MapRepositoryImpl
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
import com.example.myapplication.model.surfareas.SurfArea
import com.mapbox.geojson.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MapScreenUiState(
    val points: List<Pair<SurfArea, Point>> = emptyList(), //bruker ikke denne, men bytter den ut når vi trenger viewmodel for andre deler av mapscreen

    val windSpeed: Map<SurfArea, List<Pair<List<Int>, Double>>> = emptyMap(),
    val windGust: Map<SurfArea, List<Pair<List<Int>, Double>>> = emptyMap(),
    val windDirection: Map<SurfArea,List<Pair<List<Int>, Double>>> = emptyMap(),
    val airTemperature: Map<SurfArea,List<Pair<List<Int>, Double>>> = emptyMap(),
    val symbolCode: Map<SurfArea,List<Pair<List<Int>, String>>> = emptyMap(),
    val waveHeight: Map<SurfArea, List<Pair<List<Int>, Double>>> = emptyMap(),
    val waveDirections: Map<SurfArea, List<Pair<List<Int>, Double>>> = emptyMap(),
    val wavePeriods: Map<SurfArea, List<Double?>> = emptyMap(),
    val windPeriods: Map<SurfArea, List<Double?>> = emptyMap(),
)


class MapScreenViewModel : ViewModel() {

    private val smackLipRepository = SmackLipRepositoryImpl()
    private val _mapScreenUiState = MutableStateFlow(MapScreenUiState())
    val mapScreenUiState: StateFlow<MapScreenUiState> = _mapScreenUiState.asStateFlow()


    init {
        updateWindSpeed()
        updateWindGust()
        updateAirTemperature()
        updateSymbolCode()
        updateWindDirection()
        updateWaveHeight()
    }

    fun updateWindSpeed() {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedWindSpeed: MutableMap<SurfArea, List<Pair<List<Int>, Double>>> = mutableMapOf()
            SurfArea.entries.forEach {  surfArea ->
                val newWindSpeed = smackLipRepository.getWindSpeed(surfArea)
                updatedWindSpeed[surfArea] = newWindSpeed
            }
            _mapScreenUiState.update {
                it.copy(windSpeed = updatedWindSpeed)
            }
        }
    }

    fun updateWindGust() {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedWindGust: MutableMap<SurfArea, List<Pair<List<Int>, Double>>> = mutableMapOf()
            SurfArea.entries.forEach {surfArea ->
                val newWindGust = smackLipRepository.getWindSpeedOfGust(surfArea)
                updatedWindGust[surfArea] = newWindGust
            }
            _mapScreenUiState.update {
                it.copy(windGust = updatedWindGust)
            }
        }
    }

    fun updateWindDirection() {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedWindDirection: MutableMap<SurfArea, List<Pair<List<Int>, Double>>> = mutableMapOf()
            SurfArea.entries.forEach {surfArea ->
                val newWindDirection = smackLipRepository.getWindDirection(surfArea)
                updatedWindDirection[surfArea] = newWindDirection
            }
            _mapScreenUiState.update {
                it.copy(windDirection = updatedWindDirection)
            }
        }
    }

    fun updateAirTemperature(){
        viewModelScope.launch(Dispatchers.IO) {
            val updatedAirTemperature: MutableMap<SurfArea, List<Pair<List<Int>, Double>>> = mutableMapOf()
            SurfArea.entries.forEach { surfArea ->
                val newWaveHeight = smackLipRepository.getAirTemperature(surfArea)
                updatedAirTemperature[surfArea] = newWaveHeight
            }
            _mapScreenUiState.update {
                it.copy(airTemperature = updatedAirTemperature)
            }
        }
    }

    fun updateSymbolCode(){
        viewModelScope.launch(Dispatchers.IO) {
            val updatedSymbolCode: MutableMap<SurfArea, List<Pair<List<Int>, String>>> = mutableMapOf()
            SurfArea.entries.forEach { surfArea ->
                val newSymbolCode = smackLipRepository.getSymbolCode(surfArea)
                updatedSymbolCode[surfArea] = newSymbolCode
            }
            _mapScreenUiState.update {
                it.copy(symbolCode = updatedSymbolCode)
            }
        }
    }

    fun updateWaveHeight(){
        viewModelScope.launch(Dispatchers.IO) {
            val updatedWaveHeight: MutableMap<SurfArea, List<Pair<List<Int>, Double>>> = mutableMapOf()
            SurfArea.entries.forEach { surfArea ->
                val newWaveHeight = smackLipRepository.getWaveHeights(surfArea)
                updatedWaveHeight[surfArea] = newWaveHeight
            }
            _mapScreenUiState.update {
                it.copy(waveHeight = updatedWaveHeight)
            }
        }
    }
    fun updateWaveDirections(){
        viewModelScope.launch(Dispatchers.IO) {
            val updatedWaveDirections: MutableMap<SurfArea, List<Pair<List<Int>, Double>>> = mutableMapOf()
            SurfArea.entries.forEach { surfArea ->
                val newWaveDirection = smackLipRepository.getWaveDirections(surfArea)
                updatedWaveDirections[surfArea] = newWaveDirection
            }
            _mapScreenUiState.update {
                it.copy(waveDirections = updatedWaveDirections)
            }
        }
    }

}
