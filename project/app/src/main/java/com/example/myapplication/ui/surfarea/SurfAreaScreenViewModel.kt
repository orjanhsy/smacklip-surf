package com.example.myapplication.ui.surfarea

import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
import com.example.myapplication.model.metalerts.Features
import com.example.myapplication.ui.home.HomeScreenUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SurfAreaScreenUiState(
    val locationName : String = "",
    // val windSpeedNext7Days:,
    // val waveHeightNext7Days:,
    // val alerts: List<List<Features>>,
    // val windGustNext7Days:,
    // val alt mulig next 7 days om er relevant

)
class SurfAreaScreenViewModel {
    private val smackLipRepository = SmackLipRepositoryImpl()
    private val _surfAreaScreenUiState = MutableStateFlow(SurfAreaScreenUiState())
    val surfAreaScreenUiState: StateFlow<SurfAreaScreenUiState> = _surfAreaScreenUiState.asStateFlow()
}