package com.example.myapplication.ui.home
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
import com.example.myapplication.model.metalerts.Features
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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



}