package com.example.myapplication.ui.home
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.metalerts.Features
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeScreenUiState(
    val locationName: String = "",
    val windSpeed: Map<SurfArea, List<Pair<List<Int>, Double>>> = emptyMap(),
    val windGust: Map<SurfArea, List<Pair<List<Int>, Double>>> = emptyMap(),
    val windDirection: List<Pair<String, Double>> = emptyList(),
    val waveHeight: Map<SurfArea, List<Pair<List<Int>, Double>>> = emptyMap(),
    val allRelevantAlerts: List<List<Features>> = emptyList()
)

class HomeScreenViewModel : ViewModel() {
    private val smackLipRepository = SmackLipRepositoryImpl()
    private val _homeScreenUiState = MutableStateFlow(HomeScreenUiState())
    val homeScreenUiState: StateFlow<HomeScreenUiState> = _homeScreenUiState.asStateFlow()

    init {
        updateWindSpeed()
        updateWindGust()
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

    fun updateAlerts() {
        viewModelScope.launch(Dispatchers.IO) {
            val allAlerts: MutableList<List<Features>> = mutableListOf()
            SurfArea.entries.forEach { surfArea ->
                allAlerts.add(smackLipRepository.getRelevantAlertsFor(surfArea))
            }
            _homeScreenUiState.update {
                it.copy(allRelevantAlerts = allAlerts.toList())
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

}


