package com.example.myapplication.ui.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
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
    val relevantAlerts : List<Features> = emptyList()
)
class HomeScreenViewModel : ViewModel() {
    private val smackLipRepository = SmackLipRepositoryImpl()
    private val _homeScreenUiState = MutableStateFlow(HomeScreenUiState())
    val homeScreenUiState: StateFlow<HomeScreenUiState> = _homeScreenUiState.asStateFlow()

    init {
        updateWindSpeed()
    }
    fun updateWindSpeed(){
        viewModelScope.launch (Dispatchers.IO){
            _homeScreenUiState.update{
                val newWindSpeed = smackLipRepository.getWindSpeed()
                it.copy(windSpeed = newWindSpeed)
            }
        }
    }

    fun updateWindGust(){
        viewModelScope.launch (Dispatchers.IO){
            _homeScreenUiState.update{
                val newWindSpeed = smackLipRepository.getWindSpeedOfGust()
                it.copy(windSpeed = newWindSpeed)
            }
        }
    }

    fun updateWaveHeight(){
        viewModelScope.launch (Dispatchers.IO){
            _homeScreenUiState.update{
                val newWindSpeed = smackLipRepository.getWaveHeights(smackLipRepository.getTimeSeriesOF())
                it.copy(windSpeed = newWindSpeed)
            }
        }
    }


}