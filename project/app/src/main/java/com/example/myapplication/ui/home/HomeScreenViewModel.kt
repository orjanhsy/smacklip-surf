package com.example.myapplication.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.data.smackLip.Repository
import com.example.myapplication.data.smackLip.RepositoryImpl
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
import com.example.myapplication.model.metalerts.Alert
import com.example.myapplication.model.smacklip.AllSurfAreasOFLF
import com.example.myapplication.model.smacklip.DataAtTime
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.waveforecast.AllWavePeriods
import kotlinx.coroutines.Dispatchers
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

class HomeScreenViewModel() : ViewModel() {
    private val repo = RepositoryImpl()
    private val _favoriteSurfAreas = MutableStateFlow<List<SurfArea>>(emptyList())
    val favoriteSurfAreas: StateFlow<List<SurfArea>> = _favoriteSurfAreas // TODO: asStateFlow()?


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
            allRelevantAlerts = allRelevantAlerts
        )

    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        HomeScreenUiState()
    )



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