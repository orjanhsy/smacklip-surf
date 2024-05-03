package com.example.myapplication.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.AppContainer
import com.example.myapplication.R
import com.example.myapplication.Settings
import com.example.myapplication.model.metalerts.Alert
import com.example.myapplication.model.smacklip.AllSurfAreasOFLF
import com.example.myapplication.model.smacklip.DataAtTime
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.waveforecast.AllWavePeriods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class HomeScreenUiState(
    val wavePeriods: AllWavePeriods = AllWavePeriods(),
    val ofLfNow: Map<SurfArea, DataAtTime> = mapOf(),
    val allRelevantAlerts: Map<SurfArea, List<Alert>> = emptyMap(),
    val loading: Boolean = true
)

class HomeScreenViewModel(
    private val container: AppContainer
) : ViewModel() {
    private val _homeScreenUiState = MutableStateFlow(HomeScreenUiState())
    private val _favoriteSurfAreas = MutableStateFlow<List<SurfArea>>(emptyList())
    val homeScreenUiState: StateFlow<HomeScreenUiState> = _homeScreenUiState.asStateFlow()
    val favoriteSurfAreas: StateFlow<List<SurfArea>> = _favoriteSurfAreas
    val settings: Flow<Settings> = container.settingsRepository.settingsFlow


    init {
        loadFavoriteSurfAreas()
        updateOFLF()
        updateAlerts()
    }

    fun updateOFLF() {
        viewModelScope.launch(Dispatchers.IO) {
            _homeScreenUiState.update {state ->
                if (state.ofLfNow.isNotEmpty()) {
                    Log.d("HSVM", "Quitting 'updateOFLF', data already loaded")
                    return@launch
                }
                val allNext7Days: AllSurfAreasOFLF = container.smackLipRepository.getAllOFLF7Days()

                val newOfLfNow: Map<SurfArea, DataAtTime> = allNext7Days.next7Days.entries.associate {(sa, forecast7Days) ->
                    val times = forecast7Days.forecast[0].data.keys.sortedWith(
                        compareBy<LocalDateTime> { it.month }.thenBy { it.dayOfMonth }
                    )
                    sa to forecast7Days.forecast[0].data[times[0]]!!// TODO: !!
                }

                state.copy(
                    ofLfNow = newOfLfNow,
                    loading = false
                )
            }
        }

    }


    fun updateAlerts() {
        viewModelScope.launch(Dispatchers.IO) {
            _homeScreenUiState.update {
                it.copy(loading = true)
            }
            val allAlerts = SurfArea.entries.associateWith {
                container.smackLipRepository.getRelevantAlertsFor(it)
            }
            _homeScreenUiState.update {
                it.copy(
                    allRelevantAlerts = allAlerts,
                   // loading = false
                )
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
    fun getSurfAreaByLocationName(locationName: String): SurfArea? {
        return SurfArea.entries.firstOrNull{ it.locationName.equals(locationName, ignoreCase = true)}
    }
    fun loadFavoriteSurfAreas(){
        viewModelScope.launch {
            container.settingsRepository.settingsFlow.collect{
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
                container.settingsRepository.removeFavoriteSurfArea(surfArea.locationName)
                currentFavorites - surfArea
            } else{
                try{
                    container.settingsRepository.addFavoriteSurfArea(surfArea.locationName)
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
            container.settingsRepository.clearFavoriteSurfAreas()
            _favoriteSurfAreas.value = emptyList()
        }
    }

    class HomeScreenViewModelFactory(
        private val appContainer: AppContainer
    ) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeScreenViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return HomeScreenViewModel(appContainer) as T
            }
            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }

}