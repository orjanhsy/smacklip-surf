package com.example.myapplication.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.AppContainer
import com.example.myapplication.R
import com.example.myapplication.data.smackLip.Repository
import com.example.myapplication.data.smackLip.RepositoryImpl
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl

import com.example.myapplication.Settings
import com.example.myapplication.SmackLipApplication.Companion.container

import com.example.myapplication.model.metalerts.Alert
import com.example.myapplication.model.smacklip.AllSurfAreasOFLF
import com.example.myapplication.model.smacklip.DataAtTime
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.waveforecast.AllWavePeriods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class HomeScreenUiState(
    val ofLfNow: Map<SurfArea, DataAtTime> = mapOf(),
    val allRelevantAlerts: Map<SurfArea, List<Alert>> = emptyMap(),
    val loading: Boolean = true
)

class HomeScreenViewModel(
    private val repo: Repository

) : ViewModel() {
    private val _favoriteSurfAreas = MutableStateFlow<List<SurfArea>>(emptyList())
    val favoriteSurfAreas: StateFlow<List<SurfArea>> = _favoriteSurfAreas // TODO: asStateFlow()?
    val settings: Flow<Settings> = container.settingsRepository.settingsFlow


    val homeScreenUiState: StateFlow<HomeScreenUiState> = combine(
        repo.ofLfNext7Days,
        repo.alerts
    ) { oflf, alerts ->
        val oflfNow: Map<SurfArea, DataAtTime> = oflf.next7Days.entries.associate {
            it.key to it.value.forecast[0].data.entries.sortedBy {timeToData -> timeToData.key.hour }[0].value
        }
        val allRelevantAlerts: Map<SurfArea, List<Alert>> = alerts
        HomeScreenUiState(
            ofLfNow = oflfNow,
            allRelevantAlerts = allRelevantAlerts,
            loading = false
        )

    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        HomeScreenUiState()
    )

    suspend fun updateOfLF() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.loadOFlF()
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
}