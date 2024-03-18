package com.example.myapplication.ui.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
import com.example.myapplication.model.SurfArea
import com.example.myapplication.model.metalerts.Features
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeScreenUiState(
    val locationName : String = "",
    val windSpeed : List<Pair<String, Double>> = emptyList(),
    val windGust : List<Pair<String, Double>> = emptyList(),
    val windDirection : List<Pair<String, Double>> = emptyList(),
    val waveHeight : List<Pair<String, Double>> = emptyList(),
    val allRelevantAlerts : List<List<Features>> = emptyList()
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
            _homeScreenUiState.update {
                val newWindSpeed = smackLipRepository.getWindSpeed()
                it.copy(windSpeed = newWindSpeed)
            }
        }
    }

    fun updateWindGust() {
        viewModelScope.launch(Dispatchers.IO) {
            _homeScreenUiState.update {
                val newWindGust = smackLipRepository.getWindSpeedOfGust()
                it.copy(windGust = newWindGust)
            }
        }
    }


    fun updateWaveHeight(){
        viewModelScope.launch (Dispatchers.IO){
            _homeScreenUiState.update{
                val newWaveHeight = smackLipRepository.getWaveHeights()

                it.copy(waveHeight = newWaveHeight)
            }
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
                    "2" -> R.drawable.icon_awareness_yellow
                    "3" -> R.drawable.icon_awareness_orange
                    "4" -> R.drawable.icon_awareness_red
                    else -> R.drawable.icon_awareness_default // Hvis awarenessLevel ikke er 2, 3 eller 4
                }
            } else {
                R.drawable.icon_awareness_default // Hvis awarenessLevel er en tom streng
            }
        } catch (e: Exception) {
            R.drawable.icon_awareness_default
        }
    }

}


