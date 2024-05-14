package no.uio.ifi.in2000.team8.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import no.uio.ifi.in2000.team8.model.surfareas.SurfArea
import no.uio.ifi.in2000.team8.Settings
import no.uio.ifi.in2000.team8.data.metalerts.MetAlertsRepository
import no.uio.ifi.in2000.team8.data.settings.SettingsRepository
import no.uio.ifi.in2000.team8.data.weatherforecast.WeatherForecastRepository
import no.uio.ifi.in2000.team8.model.metalerts.Alert
import no.uio.ifi.in2000.team8.model.weatherforecast.DataAtTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team8.R

data class HomeScreenUiState(
    val ofLfNow: Map<SurfArea, DataAtTime> = mapOf(),
    val allRelevantAlerts: Map<SurfArea, List<Alert>> = emptyMap(),
)

class HomeScreenViewModel(
    private val forecastRepo: WeatherForecastRepository,
    private val alertsRepo: MetAlertsRepository,
    private val settingsRepo: SettingsRepository

) : ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            updateOfLF()
            updateWavePeriods()
            updateAlerts()
        }
    }
    private val _favoriteSurfAreas = MutableStateFlow<List<SurfArea>>(emptyList())
    val favoriteSurfAreas: StateFlow<List<SurfArea>> = _favoriteSurfAreas
    val settings: Flow<Settings> = settingsRepo.settingsFlow


    val homeScreenUiState: StateFlow<HomeScreenUiState> = combine(
        forecastRepo.ofLfNext7Days,
        alertsRepo.alerts
    ) { oflf, alerts ->
        val oflfNow: Map<SurfArea, DataAtTime> = oflf.next7Days.entries.associate {
            it.key to it.value.forecast[0].data.entries.sortedBy {timeToData -> timeToData.key.hour }[0].value
        }
        val allRelevantAlerts: Map<SurfArea, List<Alert>> = alerts
        HomeScreenUiState(
            ofLfNow = oflfNow,
            allRelevantAlerts = allRelevantAlerts,
        )

    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        HomeScreenUiState()
    )

    private suspend fun updateOfLF() {
        viewModelScope.launch(Dispatchers.IO) {
            forecastRepo.loadOFlF()
        }
    }

    private suspend fun updateWavePeriods() {
        viewModelScope.launch {
            forecastRepo.loadWavePeriods()
        }
    }

    private suspend fun updateAlerts() {
        viewModelScope.launch {
            alertsRepo.loadAllRelevantAlerts()
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

    fun getSurfAreaByLocationName(locationName: String): SurfArea? {
        return SurfArea.entries.firstOrNull{ it.locationName.equals(locationName, ignoreCase = true)}
    }
    fun loadFavoriteSurfAreas(){
        viewModelScope.launch {
            settingsRepo.settingsFlow.collect{
                Log.d("LogFavorites", "loaded favorite surf areas: ${it.favoriteSurfAreaNamesList}")
                val favoriteAreas = it.favoriteSurfAreaNamesList.mapNotNull { areaName ->
                    //konverter locationname ti surfAreaObjekt
                    val favSurfArea = getSurfAreaByLocationName(areaName)
                    if (favSurfArea == null){
                        Log.d("FavoriteList", "Failed to fetch saved Favorites $areaName")
                    }
                    favSurfArea
                }
                _favoriteSurfAreas.value= favoriteAreas
            }
        }
    }

    fun updateFavorites(surfArea: SurfArea) {
        viewModelScope.launch {
            val currentFavorites = _favoriteSurfAreas.value
            val isFavorite = currentFavorites.contains(surfArea)
            val updatedFavorites: List<SurfArea> = if (isFavorite) {
                settingsRepo.removeFavoriteSurfArea(surfArea.locationName)
                currentFavorites - surfArea
            } else{
                try{
                    settingsRepo.addFavoriteSurfArea(surfArea.locationName)
                    Log.d("CorrectAddFavorites", "Passed add to favorites")
                    currentFavorites + surfArea
                } catch(e: IllegalArgumentException){
                    Log.d("AddFavorites", "Failed to add to favorites")
                    return@launch
                }
            }
            _favoriteSurfAreas.value= updatedFavorites
        }
    }

    fun updateFavoritesIcon(surfArea: SurfArea): Int {
        return if (_favoriteSurfAreas.value.contains(surfArea)) {
            R.drawable.yellow_star_icon
        } else {
            R.drawable.empty_star_icon
        }
    }

    fun clearAllFavorites(){
        viewModelScope.launch {
            settingsRepo.clearFavoriteSurfAreas()
            _favoriteSurfAreas.value = emptyList()
        }
    }
}