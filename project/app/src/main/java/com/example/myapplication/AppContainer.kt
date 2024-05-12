package com.example.myapplication

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.example.myapplication.data.settings.SettingsRepository
import com.example.myapplication.data.settings.SettingsRepositoryImpl
import com.example.myapplication.data.settings.SettingsSerializer
import com.example.myapplication.data.smackLip.Repository
import com.example.myapplication.data.smackLip.RepositoryImpl
import com.example.myapplication.ui.info.InfoScreenViewModel


private const val DATA_STORE_FILE_NAME = "settings.pb"
interface AppContainer {
    val infoViewModel: InfoScreenViewModel
    val settingsRepository: SettingsRepository
    val stateFulRepo: Repository
}
class DefaultAppContainer(
    private val context: Context
): AppContainer {
    private val settingsStore: DataStore<Settings> =
        DataStoreFactory.create(SettingsSerializer){
            context.dataStoreFile(DATA_STORE_FILE_NAME)
        }

    override val stateFulRepo: Repository by lazy {
        RepositoryImpl()
    }

    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(settingsStore)
    }

    override val infoViewModel: InfoScreenViewModel by lazy{
        InfoScreenViewModel(settingsRepository)
    }


}