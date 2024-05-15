package com.example.myapplication

import androidx.datastore.core.DataStore
import com.example.myapplication.data.settings.SettingsRepository
import com.example.myapplication.data.settings.SettingsRepositoryImpl
import junit.framework.TestCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class DataStoreTests {
    //tester protobuf-filen
    private lateinit var settingsStore: DataStore<Settings>
    private lateinit var settingsRepository: SettingsRepository

//    @Before
//    fun setUp(){
//        settingsStore = createDataStore()
//        settingsRepository = SettingsRepositoryImpl(settingsStore)
//    }
//    private fun createDataStore(): DataStore<Settings> {
//        val settings = Settings.newBuilder().setTest(0.0).setDarkMode(false).build()
//        val settingsFlow = MutableStateFlow(settings)
//        return object : DataStore<Settings> {
//            override suspend fun updateData(transform: suspend (t: Settings) -> Settings):Settings {
//                val updatedSettings = transform(settingsFlow.value)
//                settingsFlow.value = updatedSettings
//                return updatedSettings
//            }
//
//            override val data: Flow<Settings> = settingsFlow
//        }
//
//    }
//
//
//
//    @Test
//    fun testSetTest() = runBlocking {
//        val testValue = 10.0
//        settingsRepository.setTest(testValue)
//
//        val updatedSettings = settingsStore.data.first()
//        TestCase.assertEquals(testValue, updatedSettings.test, 0.0)
//    }
//
//    @Test
//    fun testSetDarkMode() = runBlocking{
//        val darkModeValue = true
//        settingsRepository.setDarkMode(darkModeValue)
//
//        val updatedSettings = settingsStore.data.first()
//        TestCase.assertEquals(darkModeValue, updatedSettings.darkMode)
//    }
//    @Test
//    fun testSerialization() = runBlocking{
//        val settingsSerializer = SettingsSerializer()
//        val testValue = 42.0
//        val originalSettings = Settings.newBuilder().setTest(testValue).build()
//        val outputStream = ByteArrayOutputStream()
//        settingsSerializer.writeTo(originalSettings, outputStream)
//        val serializedSettings = outputStream.toByteArray()
//
//        val inputStream = ByteArrayInputStream(serializedSettings)
//        val deserializedSettings = settingsSerializer.readFrom(inputStream)
//
//        assertEquals(originalSettings, deserializedSettings)
//
//    }
//
//    @Test
//    fun testAddFavorite() = runBlocking {
//        val favorite = "HODDEVIK"
//        settingsRepository.addFavoriteSurfArea(favorite)
//
//        val updatedSettings = settingsStore.data.first()
//        TestCase.assertTrue(updatedSettings.favoriteSurfAreasList.contains(favorite))
//    }
}