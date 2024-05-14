package no.uio.ifi.in2000.team8

import com.example.myapplication.data.metalerts.MetAlertsDataSource
import kotlinx.coroutines.runBlocking
import org.junit.Test

class FinalTests {

    //metalerts

    @Test
    fun metAlertsDataSourceHandlesNon200() = runBlocking {
        val metAlertsDataSource = MetAlertsDataSource("error_url")
        val response = try { metAlertsDataSource.fetchMetAlertsData() }
        catch (e: Exception) {
            null
        }
        assert(response==null)
    }


}