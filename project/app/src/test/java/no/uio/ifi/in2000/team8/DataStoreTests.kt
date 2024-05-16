package no.uio.ifi.in2000.team8

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.team8.data.settings.SettingsRepositoryImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class DataStoreTests {

    private lateinit var dataStore: DataStore<Settings>
    private lateinit var settingsRepo: SettingsRepositoryImpl

    //creating a test data store for unit tests for isolation
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
    fun testUpdateTheme(){
        runTest {
            val theme = Settings.Theme.DARK
            settingsRepo.updateTheme(theme)
            val settings = dataStore.data.first()
            assertEquals(theme, settings.theme)
        }
    }

    @Test
    fun testAddFavorites(){
        runTest {
            val favorite = "HODDEVIK"
            val favoriteTwo = "ERVIKA"
            settingsRepo.addFavoriteSurfArea(favorite)
            settingsRepo.addFavoriteSurfArea(favoriteTwo)
            val settings = dataStore.data.first()
            assertTrue(settings.favoriteSurfAreaNamesList.contains(favorite))
            assertEquals(2,settings.favoriteSurfAreaNamesList.size )
        }
    }

    @Test
    fun testAddAndRemoveSurfAreas(){
        runTest {
            val favorite = "ERVIKA"
            val favoriteTwo = "HODDEVIK"
            settingsRepo.addFavoriteSurfArea(favorite)
            settingsRepo.removeFavoriteSurfArea(favorite)
            settingsRepo.addFavoriteSurfArea(favoriteTwo)
            val settings = dataStore.data.first()
            assertTrue(settings.favoriteSurfAreaNamesList.contains(favoriteTwo))
            assertEquals(1, settings.favoriteSurfAreaNamesList.size)
        }
    }

    @Test
    fun testClearingAllSurfAreas(){
        runTest {
            val favorite = "ERVIKA"
            val favoriteTwo = "HODDEVIK"
            val favoriteThree = "BORESANDEN"
            settingsRepo.addFavoriteSurfArea(favorite)
            settingsRepo.addFavoriteSurfArea(favoriteTwo)
            settingsRepo.addFavoriteSurfArea(favoriteThree)
            settingsRepo.clearFavoriteSurfAreas()
            val settings = dataStore.data.first()
            assertEquals(0, settings.favoriteSurfAreaNamesList.size)
        }
    }




}