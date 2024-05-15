package com.example.myapplication.ui.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Settings
import com.example.myapplication.data.settings.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class InfoScreenViewModel(
    private val settingsRepo: SettingsRepository,
) : ViewModel() {
    val settings: Flow<Settings> = settingsRepo.settingsFlow
    val isDarkThemEnabled: StateFlow<Boolean> = settingsRepo.settingsFlow
        .map {  it.theme == Settings.Theme.DARK}
        .stateIn(viewModelScope, SharingStarted.Lazily, false)




    fun updateTheme(theme: Settings.Theme){
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepo.updateTheme(theme)
        }
    }

}
