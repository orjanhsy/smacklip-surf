package no.uio.ifi.in2000.team8.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team8.R
import no.uio.ifi.in2000.team8.Settings
import no.uio.ifi.in2000.team8.data.metalerts.MetAlertsRepository
import no.uio.ifi.in2000.team8.data.settings.SettingsRepository
import no.uio.ifi.in2000.team8.data.weatherforecast.WeatherForecastRepository
import no.uio.ifi.in2000.team8.model.surfareas.SurfArea
import no.uio.ifi.in2000.team8.model.weatherforecast.DataAtTime

data class HomeScreenUiState(
    val ofLfNow: Map<SurfArea, DataAtTime> = mapOf(),
)

class HomeScreenViewModel(
    private val forecastRepo: WeatherForecastRepository,
    private val settingsRepo: SettingsRepository,
    private val alertsRepo: MetAlertsRepository

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


    val homeScreenUiState: StateFlow<HomeScreenUiState> = forecastRepo.ofLfForecast.map{ ofLf ->
        val ofLfNow: Map<SurfArea, DataAtTime> = try {
            ofLf.forecasts.entries.associate {
                it.key to it.value.dayForecasts[0].data.entries.sortedBy { timeToData -> timeToData.key.hour }[0].value
            }
        } catch (e: IndexOutOfBoundsException) {
            mapOf()
        }
        HomeScreenUiState(
            ofLfNow = ofLfNow,
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

    private fun getSurfAreaByLocationName(locationName: String): SurfArea? {
        return SurfArea.entries.firstOrNull{ it.locationName.equals(locationName, ignoreCase = true)}
    }
    fun loadFavoriteSurfAreas(){
        viewModelScope.launch {
            settingsRepo.settingsFlow.collect{
                val favoriteAreas = it.favoriteSurfAreaNamesList.mapNotNull { areaName ->
                    val favSurfArea = getSurfAreaByLocationName(areaName)
                    if (favSurfArea == null){
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
                    currentFavorites + surfArea
                } catch(e: IllegalArgumentException){
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