package com.example.myapplication.data.settings

import androidx.datastore.core.DataStore
import com.example.myapplication.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository{
    val settingsFlow: Flow<Settings>
    suspend fun setTest(test: Double)
    suspend fun setDarkMode(enabled: Boolean)
    suspend fun addFavoriteSurfArea(favorite: String)
    suspend fun removeFavoriteSurfArea(favorite: String)
}
class SettingsRepositoryImpl (
    private val settingsStore: DataStore<Settings>
): SettingsRepository {
    override val settingsFlow: Flow<Settings> = settingsStore.data
    override suspend fun setTest(test: Double) {
        settingsStore.updateData { it.toBuilder().setTest(test).build() }
    }

    override suspend fun setDarkMode(enabled: Boolean){
        settingsStore.updateData { it.toBuilder().setDarkMode(enabled).build() }
    }


    override suspend fun addFavoriteSurfArea(favorite: String){
        settingsStore.updateData {
            val builder = it.toBuilder()
            if (!builder.favoriteSurfAreasList.contains(favorite)) {
                val newList = builder.favoriteSurfAreasList.toMutableList().apply { add(favorite) }
                builder.addAllFavoriteSurfAreas(newList)
            }
            builder.build()
        }
    }

    override suspend fun removeFavoriteSurfArea(favorite: String) {
        settingsStore.updateData {
            val builder = it.toBuilder()
            val newList = builder.favoriteSurfAreasList.filter { it != favorite }
            builder.clearFavoriteSurfAreas().addAllFavoriteSurfAreas(newList)
            builder.build()
        }
    }
}