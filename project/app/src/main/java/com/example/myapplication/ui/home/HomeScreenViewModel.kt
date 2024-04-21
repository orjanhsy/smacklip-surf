package com.example.myapplication.ui.home

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
import com.example.myapplication.model.metalerts.Features
import com.example.myapplication.model.surfareas.SurfArea
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeScreenUiState(
    val locationName: String = "",
    val windSpeed: Map<SurfArea, List<Pair<List<Int>, Double>>> = emptyMap(),
    val windGust: Map<SurfArea, List<Pair<List<Int>, Double>>> = emptyMap(),
    val windDirection: Map<SurfArea,List<Pair<List<Int>, Double>>> = emptyMap(),
    val waveHeight: Map<SurfArea, List<Pair<List<Int>, Double>>> = emptyMap(),
    val waveDirections: Map<SurfArea, List<Pair<List<Int>, Double>>> = emptyMap(),
    val wavePeriods: Map<SurfArea, List<Double?>> = emptyMap(),
    val windPeriods: Map<SurfArea, List<Double?>> = emptyMap(),
    val allRelevantAlerts: Map<SurfArea, List<Features>> = emptyMap()
)

class HomeScreenViewModel : ViewModel() {
    private val smackLipRepository = SmackLipRepositoryImpl()
    private val _homeScreenUiState = MutableStateFlow(HomeScreenUiState())
    private val _searchQuery = MutableStateFlow("")
    private val _favoriteSurfAreas = MutableStateFlow<List<SurfArea>>(emptyList())
    val homeScreenUiState: StateFlow<HomeScreenUiState> = _homeScreenUiState.asStateFlow()
    val searchQuery = _searchQuery.asStateFlow()
    val favoriteSurfAreas: StateFlow<List<SurfArea>> = _favoriteSurfAreas

    init {
        updateWindSpeed()
        updateWindGust()
        updateWindDirection()
        updateWaveHeight()
        updateAlerts()
    }

    fun updateWindSpeed() {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedWindSpeed: MutableMap<SurfArea, List<Pair<List<Int>, Double>>> = mutableMapOf()
            SurfArea.entries.forEach {  surfArea ->
                val newWindSpeed = smackLipRepository.getWindSpeed(surfArea)
                updatedWindSpeed[surfArea] = newWindSpeed
            }
            _homeScreenUiState.update {
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
            _homeScreenUiState.update {
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
            _homeScreenUiState.update {
                it.copy(windDirection = updatedWindDirection)
            }
        }
    }

    fun updateWaveHeight(){
        viewModelScope.launch(Dispatchers.IO) {
            val updatedWaveHeight: MutableMap<SurfArea, List<Pair<List<Int>, Double>>> = mutableMapOf()
            SurfArea.entries.forEach { surfArea ->
                //Log.d("updatedWaveHeight", "processing for $surfArea")
                val newWaveHeight = smackLipRepository.getWaveHeights(surfArea)
                updatedWaveHeight[surfArea] = newWaveHeight
                //Log.d("updatedAllWaveHeights", "WaveHeight for $surfArea: $newWaveHeight")
            }
            _homeScreenUiState.update {
                it.copy(waveHeight = updatedWaveHeight)
            }
            //Log.d("updatedWaveHeight", "WaveHeight Updated: $updatedWaveHeight")
        }

    }
    fun updateWaveDirections(){
        viewModelScope.launch(Dispatchers.IO) {
            val updatedWaveDirections: MutableMap<SurfArea, List<Pair<List<Int>, Double>>> = mutableMapOf()
            SurfArea.entries.forEach { surfArea ->
                val newWaveDirection = smackLipRepository.getWaveDirections(surfArea)
                updatedWaveDirections[surfArea] = newWaveDirection
            }
            _homeScreenUiState.update {
                it.copy(waveDirections = updatedWaveDirections)
            }
        }
    }

    fun updateWavePeriods() {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedWavePeriods: MutableMap<SurfArea, List<Double?>> = mutableMapOf()
            SurfArea.entries.forEach { surfArea ->
                val newWavePeriods = smackLipRepository.getWavePeriodsNext3DaysForArea(surfArea)
                updatedWavePeriods[surfArea] = newWavePeriods
            }
            _homeScreenUiState.update {
                it.copy(wavePeriods = updatedWavePeriods)
            }
        }
    }


    fun updateAlerts() {
        viewModelScope.launch(Dispatchers.IO) {
            val allAlerts = SurfArea.entries.associateWith {
                smackLipRepository.getRelevantAlertsFor(it)
            }
            _homeScreenUiState.update {
                it.copy(allRelevantAlerts = allAlerts)
            }
        }
    }
    fun getIconBasedOnAwarenessLevel(awarenessLevel: String): Int {
        return try {
            if (awarenessLevel.isNotEmpty()) {
                val firstChar = awarenessLevel.firstOrNull()?.toString()

                when (firstChar) {
                    "2" -> R.drawable.icon_awareness_yellow_outlined
                    "3" -> R.drawable.icon_awareness_orange
                    "4" -> R.drawable.icon_awareness_red
                    else -> R.drawable.icon_awareness_default // Hvis awarenessLevel ikke er 2, 3 eller 4
                }
            } else {
                R.drawable.icon_awareness_default // Hvis awarenessLevel er en tom String
            }
        } catch (e: Exception) {
            R.drawable.icon_awareness_default
        }
    }

    /* val searchResults: StateFlow<List<SurfArea>> =
        snapshotFlow { searchQuery }
            .combine(flowOf(listOf(SurfArea.entries))) { searchQuery, surfAreas ->
                when {
                    searchQuery.isNotEmpty() -> surfAreas.filter { surfArea ->
                        surfArea.locationName.contains(searchQuery, ignoreCase = true)
                    }
                    else -> surfAreas
                }
            }.stateIn(
                scope = viewModelScope,
                initialValue = emptyList()
            )

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
    } */

    fun updateFavorites(surfArea: SurfArea) {
        if (_favoriteSurfAreas.value.contains(surfArea)) {
            _favoriteSurfAreas.value -= surfArea
        } else {
            _favoriteSurfAreas.value += surfArea
        }
    }

    fun updateFavoritesIcon(surfArea: SurfArea): Int {
        return if (_favoriteSurfAreas.value.contains(surfArea)) {
            R.drawable.yellow_star_icon
        } else {
            R.drawable.empty_star_icon
        }
    }
}