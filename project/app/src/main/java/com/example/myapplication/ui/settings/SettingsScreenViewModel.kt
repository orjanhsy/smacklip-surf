package com.example.myapplication.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.AppContainer
import com.example.myapplication.Settings
import com.example.myapplication.data.settings.SettingsRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    init {
        viewModelScope.launch {
            container.settingsRepository.settingsFlow.collect{
                _settingsUiState.value = SettingsUiState.Loaded(it)
            }
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

    fun setTest(test: Double){
        viewModelScope.launch {
            try {
                container.settingsRepository.setTest(test)
            } catch (e: Exception){
                _settingsUiState.value = SettingsUiState.Error("Failed to update test value")
            }
        }
    }
    fun setDarkMode(enabled: Boolean){
        viewModelScope.launch{
            try {
                container.settingsRepository.setDarkMode(enabled)
            } catch (e: Exception){
                _settingsUiState.value = SettingsUiState.Error("Failed to update dark mode")
            }
        }
    }

    fun addFavoriteSurfArea(favorite: String){
        viewModelScope.launch {
            try {
                container.settingsRepository.addFavoriteSurfArea(favorite)
            } catch (e: Exception){
                _settingsUiState.value = SettingsUiState.Error("Failed to add favorite surf area")
            }
        }
    }

    fun removeFavoriteSurfArea(favorite: String){
        viewModelScope.launch {
            try {
                container.settingsRepository.removeFavoriteSurfArea(favorite)
            } catch (e: Exception){
                _settingsUiState.value = SettingsUiState.Error("Failed to remove favorite surf area")
            }
        }
    }


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
