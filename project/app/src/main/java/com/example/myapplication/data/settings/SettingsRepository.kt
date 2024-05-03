package com.example.myapplication.data.settings

import androidx.datastore.core.DataStore
import com.example.myapplication.Settings
import com.example.myapplication.model.surfareas.SurfArea
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface SettingsRepository{
    val settingsFlow: Flow<Settings>
    suspend fun setTest(test: Double)
    suspend fun setDarkMode(enabled: Boolean)
    suspend fun clearFavoriteSurfAreas()
    suspend fun getFavoriteSurfAreas(): Flow<List<SurfArea>>
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

    override suspend fun clearFavoriteSurfAreas(){
        settingsStore.updateData {
            it.toBuilder().clearFavoriteSurfAreaNames().build()
        }
    }

    override suspend fun getFavoriteSurfAreas(): Flow<List<SurfArea>> {
        return settingsStore.data.map {
            it.favoriteSurfAreaNamesList.mapNotNull {name ->
                try{
                    SurfArea.valueOf(name)
                }catch (e: IllegalArgumentException){
                    null
                }

            }
        }
    }


    override suspend fun setDarkMode(enabled: Boolean){
        settingsStore.updateData { it.toBuilder().setDarkMode(enabled).build() }
    }




    override suspend fun addFavoriteSurfArea(favorite: String){
        settingsStore.updateData {
            val builder = it.toBuilder()
                .addFavoriteSurfAreaNames(favorite)
            builder.build()
        }
    }

    override suspend fun removeFavoriteSurfArea(favorite: String) {
        settingsStore.updateData {
            val updatedList = it.favoriteSurfAreaNamesList.toMutableList().apply {
                remove(favorite)
            }
            it.toBuilder()
                .clearFavoriteSurfAreaNames()
                .addAllFavoriteSurfAreaNames(updatedList)
                .build()
        }
    }
}




