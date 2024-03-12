package com.example.myapplication.data.metalerts

import com.example.myapplication.model.metalerts.Features
interface MetAlertsRepository{
    suspend fun getFeatures(): List<Features>

}
class MetAlertsRepositoryImpl (

    private val metAlertsDataSource : MetAlertsDataSource = MetAlertsDataSource()
) : MetAlertsRepository {
    override suspend fun getFeatures(): List<Features> {
        return metAlertsDataSource.fetchMetAlertsData().features
    }
}
