package com.example.myapplication.ui.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
import com.example.myapplication.model.locationforecast.DataLF
import com.example.myapplication.model.metalerts.Features
import com.example.myapplication.model.oceanforecast.DataOF
import com.example.myapplication.model.smacklip.AllSurfAreasOFLF
import com.example.myapplication.model.smacklip.DayData
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.waveforecast.AllWavePeriods
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import kotlin.system.exitProcess

data class HomeScreenUiState(
    val wavePeriods: AllWavePeriods = AllWavePeriods(),
    val ofLfToday: Map<SurfArea, DayData> = mapOf(),
    val allRelevantAlerts: Map<SurfArea, List<Features>> = emptyMap(),
    val loading: Boolean = false
)

class HomeScreenViewModel : ViewModel() {
    private val smackLipRepository = SmackLipRepositoryImpl()
    private val _homeScreenUiState = MutableStateFlow(HomeScreenUiState())
    private val _favoriteSurfAreas = MutableStateFlow<List<SurfArea>>(emptyList())
    val homeScreenUiState: StateFlow<HomeScreenUiState> = _homeScreenUiState.asStateFlow()
    val favoriteSurfAreas: StateFlow<List<SurfArea>> = _favoriteSurfAreas

    init {
        updateOFLF()
        updateAlerts()
    }

    fun updateOFLF() {

        viewModelScope.launch(Dispatchers.IO) {
            _homeScreenUiState.update {
                val allNext7Days: AllSurfAreasOFLF = smackLipRepository.getAllOFLF7Days()

                val newOfLfToday: Map<SurfArea, DayData> = allNext7Days.next7Days.entries.associate {(sa, forecast7Days) ->
                    sa to forecast7Days.forecast7Days[0]
                }
                it.copy(
                    ofLfToday = newOfLfToday,
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
                smackLipRepository.getRelevantAlertsFor(it)
            }
            _homeScreenUiState.update {
                it.copy(
                    allRelevantAlerts = allAlerts,
                    loading = false)
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