package no.uio.ifi.in2000.team8.data.settings

import androidx.datastore.core.DataStore
import com.example.myapplication.Settings
import com.example.myapplication.Settings.Theme
import com.example.myapplication.model.surfareas.SurfArea
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface SettingsRepository{
    val settingsFlow: Flow<Settings>
    suspend fun updateTheme(theme: Theme)
    suspend fun clearFavoriteSurfAreas()
    suspend fun getFavoriteSurfAreas(): Flow<List<SurfArea>>
    suspend fun addFavoriteSurfArea(favorite: String)
    suspend fun removeFavoriteSurfArea(favorite: String)
}
class SettingsRepositoryImpl (
    private val settingsStore: DataStore<Settings>
): SettingsRepository {
    override val settingsFlow: Flow<Settings> = settingsStore.data


    override suspend fun updateTheme(theme: Theme) {
        settingsStore.updateData {
            it.toBuilder().setTheme(theme).build()
        }

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




