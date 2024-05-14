package com.example.myapplication

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.example.myapplication.data.metalerts.MetAlertsRepository
import com.example.myapplication.data.metalerts.MetAlertsRepositoryImpl
import com.example.myapplication.data.settings.SettingsRepository
import com.example.myapplication.data.settings.SettingsRepositoryImpl
import com.example.myapplication.data.settings.SettingsSerializer
import com.example.myapplication.data.weatherForecast.WeatherForecastRepository
import com.example.myapplication.data.weatherForecast.WeatherForecastRepositoryImpl
import com.example.myapplication.ui.info.InfoScreenViewModel


private const val DATA_STORE_FILE_NAME = "settings.pb"
interface AppContainer {
    val infoViewModel: InfoScreenViewModel
    val settingsRepo: SettingsRepository
    val stateFulRepo: WeatherForecastRepository
    val alertsRepo: MetAlertsRepository
}
class DefaultAppContainer(
    private val context: Context
): AppContainer {
    private val settingsStore: DataStore<Settings> =
        DataStoreFactory.create(SettingsSerializer){
            context.dataStoreFile(DATA_STORE_FILE_NAME)
        }
    override val settingsRepo: SettingsRepository by lazy {
        SettingsRepositoryImpl(settingsStore)
    }

    override val stateFulRepo: WeatherForecastRepository by lazy {
        WeatherForecastRepositoryImpl()
    }

    override val alertsRepo: MetAlertsRepository by lazy {
        MetAlertsRepositoryImpl()
    }

    override val infoViewModel: InfoScreenViewModel by lazy {
        InfoScreenViewModel(settingsRepo)
    }


}