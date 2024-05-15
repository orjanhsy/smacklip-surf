package no.uio.ifi.in2000.team8

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.team8.data.settings.SettingsRepositoryImpl
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class DataStoreTests {
    //Making a test data store
    private lateinit var dataStore: DataStore<Settings>
    private lateinit var settingsRepo: SettingsRepositoryImpl

    private fun createDataStore(): DataStore<Settings>{
        val settings = Settings.getDefaultInstance()
        val settingsFlow = MutableStateFlow(settings)
        return object : DataStore<Settings> {
            override suspend fun updateData(transform: suspend (t: Settings) -> Settings): Settings {
                val updatedSettings = transform(settingsFlow.value)
                settingsFlow.value = updatedSettings
                return updatedSettings
            }

            override val data: Flow<Settings> = settingsFlow

        }
    }


    @Before
    fun setup(){
        dataStore = createDataStore()
        settingsRepo = SettingsRepositoryImpl(dataStore)
    }

    @Test
    fun testAddFavorites(){
        runTest {
            val favorite = "HODDEVIK"
            settingsRepo.addFavoriteSurfArea(favorite)
            settingsRepo.settingsFlow.first { settings ->
                settings.favoriteSurfAreaNamesList.contains(favorite)
            }
            val settings = dataStore.data.first()
            assertTrue(settings.favoriteSurfAreaNamesList.contains(favorite))
        }
    }



}