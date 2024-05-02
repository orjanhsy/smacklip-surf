package com.example.myapplication.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
import com.example.myapplication.model.metalerts.Alert
import com.example.myapplication.model.smacklip.AllSurfAreasOFLF
import com.example.myapplication.model.smacklip.DataAtTime
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.waveforecast.AllWavePeriods
import kotlinx.coroutines.Dispatchers
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

class HomeScreenViewModel() : ViewModel() {
    private val smackLipRepository = SmackLipRepositoryImpl()
    private val _homeScreenUiState = MutableStateFlow(HomeScreenUiState())
    private val _favoriteSurfAreas = MutableStateFlow<List<SurfArea>>(emptyList())
    val homeScreenUiState: StateFlow<HomeScreenUiState> = _homeScreenUiState.asStateFlow()
    val favoriteSurfAreas: StateFlow<List<SurfArea>> = _favoriteSurfAreas // TODO: asStateFlow()?

    init {
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
                val allNext7Days: AllSurfAreasOFLF = smackLipRepository.getAllOFLF7Days()

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
                smackLipRepository.getRelevantAlertsFor(it)
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