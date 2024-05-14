package com.example.myapplication.data.oceanforecast

import android.util.Log
import com.example.myapplication.model.oceanforecast.DataOF
import com.example.myapplication.model.oceanforecast.TimeserieOF
import com.example.myapplication.model.surfareas.SurfArea
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException

private const val TAG = "OFREPO"
interface OceanForecastRepository{
    suspend fun getTimeSeries(surfArea: SurfArea): List<Pair<String, DataOF>>
}

class OceanForecastRepositoryImpl(
    private val dataSource: OceanForecastDataSource = OceanForecastDataSource()
): OceanForecastRepository {

    override suspend fun getTimeSeries(surfArea: SurfArea): List<Pair<String, DataOF>> {
        //gets list of timeSeries objects, containing time and data
        val timeSeries: List<TimeserieOF> = try {
            dataSource.fetchOceanforecast(surfArea).properties.timeseries
        }
        catch(e: RedirectResponseException) {
            // 3xx
            Log.e(TAG, "Failed get timeSeries for $surfArea. 3xx-error. Cause: ${e.message}")
            throw e
        }
        catch (e: ClientRequestException) {
            // 4xx
            Log.e(TAG, "Failed get timeSeries for $surfArea. 4xx-error. Cause: ${e.message}")
            throw e
        }
        catch(e: ServerResponseException) {
            // 5xx
            Log.e(TAG, "Failed get timeSeries for $surfArea. 5xx-error. Cause: ${e.message}")
            throw e
        }
        catch (e: Exception) {
            Log.e(TAG, "Failed get timeSeries for $surfArea. Unknown error. Cause: ${e.message}")
            throw e
        }

        //returns a list of time mapped to data
        return timeSeries.map { it.time to it.data }
    }
}