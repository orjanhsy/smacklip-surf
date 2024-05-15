package com.example.myapplication

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.data.settings.SettingsRepositoryImpl
import com.example.myapplication.data.settings.SettingsSerializer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File


const val TEST_DATA_STORE_FILE_NAME = "testSettings.pb"


private val scheduler = TestCoroutineScheduler()
private val testScope = TestScope(scheduler)
private var testContext: Context = ApplicationProvider.getApplicationContext()

@RunWith(AndroidJUnit4::class)
class DataStoreTests {
    //Making a test data store
    private lateinit var dataStore: DataStore<Settings>
    private lateinit var settingsRepo: SettingsRepositoryImpl

    private fun createDataStore(){
        dataStore = DataStoreFactory.create(
            produceFile = {
                testContext.dataStoreFile(TEST_DATA_STORE_FILE_NAME)
            },
            serializer = SettingsSerializer
        )
        settingsRepo = SettingsRepositoryImpl(dataStore)
    }


    @Before
    fun setup(){
        createDataStore()
    }

    @Test
    fun testAddFavorites(){
        runTest {
            testScope.launch {
                val favorite = "HODDEVIK"
                settingsRepo.addFavoriteSurfArea(favorite)
                val settings = dataStore.data.first()
                assertTrue(settings.favoriteSurfAreaNamesList.contains(favorite))

            }
        }
    }

    @After
    fun cleanup(){
        File(testContext.filesDir, "datastore").deleteRecursively()
    }
}