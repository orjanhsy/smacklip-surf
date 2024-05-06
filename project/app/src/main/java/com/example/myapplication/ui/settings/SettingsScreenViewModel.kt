package com.example.myapplication.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.AppContainer
import com.example.myapplication.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class SettingsUiState{
    object Loading : SettingsUiState()
    data class Loaded(val settings: Settings): SettingsUiState()
    data class Error(val message: String): SettingsUiState()
}
class SettingsScreenViewModel(
    private val container: AppContainer,
) : ViewModel() {
    private val _settingsUiState: MutableStateFlow<SettingsUiState> =
        MutableStateFlow(SettingsUiState.Loading)
    val settingsUiState: StateFlow<SettingsUiState> = _settingsUiState.asStateFlow()
    val settings: Flow<Settings> = container.settingsRepository.settingsFlow
    val isDarkThemEnabled: StateFlow<Boolean> = container.settingsRepository.settingsFlow
        .map {  it.theme == Settings.Theme.DARK}
        .stateIn(viewModelScope, SharingStarted.Lazily, false)



    init {
        viewModelScope.launch {
            container.settingsRepository.settingsFlow.collect{
                _settingsUiState.value = SettingsUiState.Loaded(it)
            }
        }
    }

    fun updateTheme(theme: Settings.Theme){
        viewModelScope.launch {
            container.settingsRepository.updateTheme(theme)
            Log.d("Dark Mode", "Successfully updated theme to: $theme")
        }
    }

    /*
    //test med midlertidig datastore
    private fun createDataStore(): DataStore<Settings>{
        val settings = Settings.newBuilder().setTest(0.0).setDarkMode(false).build()
        val settingsFlow = MutableStateFlow(settings)
        return object : DataStore<Settings> {
            override suspend fun updateData(transform: suspend (t: Settings) -> Settings):Settings {
                val updatedSettings = transform(settingsFlow.value)
                settingsFlow.value = updatedSettings
                return updatedSettings
            }

            override val data: Flow<Settings> = settingsFlow
        }

    }

     */

    /*
    fun setDarkMode(enabled: Boolean){
        viewModelScope.launch{
            try {
                container.settingsRepository.setDarkMode(enabled)
            } catch (e: Exception){
                _settingsUiState.value = SettingsUiState.Error("Failed to update dark mode")
            }
        }
    }

     */




    class SettingsViewModelFactory(
        private val appContainer: AppContainer
    ) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsScreenViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return SettingsScreenViewModel(appContainer) as T
            }
            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }


}
