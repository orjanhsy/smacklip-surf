package com.example.myapplication

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.example.myapplication.data.settings.SettingsRepository
import com.example.myapplication.data.settings.SettingsRepositoryImpl
import com.example.myapplication.data.settings.SettingsSerializer


private const val DATA_STORE_FILE_NAME = "settings.pb"
interface AppContainer {
    val settingsRepository: SettingsRepository
}
class DefaultAppContainer(
    private val context: Context
): AppContainer {
    private val settingsStore: DataStore<Settings> =
        DataStoreFactory.create(SettingsSerializer){
            context.dataStoreFile(DATA_STORE_FILE_NAME)
        }

    override val settingsRepository: SettingsRepository =
        SettingsRepositoryImpl(settingsStore)


}