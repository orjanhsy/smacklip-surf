package com.example.myapplication.ui.info

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.AppContainer
import com.example.myapplication.Settings
import com.example.myapplication.SmackLipApplication.Companion.container
import com.example.myapplication.data.settings.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class InfoUiState{
    data object Loading : InfoUiState()
    data class Loaded(val settings: Settings): InfoUiState()
}
class InfoScreenViewModel(
    private val settingsRepo: SettingsRepository,
) : ViewModel() {
    private val _infoUiState: MutableStateFlow<InfoUiState> =
        MutableStateFlow(InfoUiState.Loading)
    val infoUiState: StateFlow<InfoUiState> = _infoUiState.asStateFlow()
    val settings: Flow<Settings> = settingsRepo.settingsFlow
    val isDarkThemEnabled: StateFlow<Boolean> = settingsRepo.settingsFlow
        .map {  it.theme == Settings.Theme.DARK}
        .stateIn(viewModelScope, SharingStarted.Lazily, false)



    init {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepo.settingsFlow.collect{
                _infoUiState.value = InfoUiState.Loaded(it)
            }
        }
    }

    fun updateTheme(theme: Settings.Theme){
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepo.updateTheme(theme)
            Log.d("Dark Mode", "Successfully updated theme to: $theme")
        }
    }

}
