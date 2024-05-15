package no.uio.ifi.in2000.team8

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import no.uio.ifi.in2000.team8.data.metalerts.MetAlertsRepository
import no.uio.ifi.in2000.team8.data.metalerts.MetAlertsRepositoryImpl
import no.uio.ifi.in2000.team8.data.settings.SettingsRepository
import no.uio.ifi.in2000.team8.data.settings.SettingsRepositoryImpl
import no.uio.ifi.in2000.team8.data.settings.SettingsSerializer
import no.uio.ifi.in2000.team8.data.weatherforecast.WeatherForecastRepository
import no.uio.ifi.in2000.team8.data.weatherforecast.WeatherForecastRepositoryImpl
import no.uio.ifi.in2000.team8.ui.info.InfoScreenViewModel


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