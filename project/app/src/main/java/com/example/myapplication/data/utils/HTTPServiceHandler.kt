package com.example.myapplication.data.utils

object HTTPServiceHandler {
    //Global API authenticator
    ***REMOVED***
    ***REMOVED***

    //LocationForecast
    const val LOCATION_FORECAST_URL = "https://gw-uio.intark.uh-it.no/in2000/weatherapi/locationforecast/2.0/complete"

    //MetAlerts
    const val METALERTS_URL = "https://in2000.api.met.no/weatherapi/metalerts/2.0/current.json"

    //Oceanforecast
    const val OCEAN_FORECAST_URL = "https://gw-uio.intark.uh-it.no/in2000/weatherapi/oceanforecast/2.0/complete"


    //-----------------------WaveForecast-----------------------
    const val WF_BASE_URL = "https://www.barentswatch.no/bwapi/"
    const val WF_ACCESS_TOKEN_URL = "https://id.barentswatch.no/connect/token"

    //--------paths for WF--------

    //returnerer alle tilgjengelige modeller/fairways (IKKE GJORT SERIALIZABLE) (treig)
    const val WF_ALL_FAIRWAYS_URL = "v2/geodata/waveforecast/fairway" // Ingen parameter

    //returnerer alle tilgjengelige timestamps
    const val WF_AVALIABLE_ALL_URL = "v1/geodata/waveforecast/available/all" // Ingen parameter

    //returnerer  tilgjengelige timestamps for en fairway (IKKE GJORT SERIALIZABLE)
    const val WF_AVALIABLE_URL = "v1/geodata/waveforecast/available" // ?modelname&fairwayid

    //returnerer bolgetilsdtander for et bestemt punkt
    const val WF_POINT_FORECAST_URL = "v1/geodata/waveforecast/pointforecast" // ?modelname&pointId&time

    //returnerer alle pointforecasts tilgjenglig for et bestemt timestamp
    const val WF_ALL_POINT_FORECASTS_URL = "v1/geodata/waveforecast/pointforecasts" // ?time

    //returnerer bølgetilstander for et bestemt punkt, med all data de har fremover i tid.
    const val WF_CLOSEST_ALL_TIME_URL = "v1/waveforecastpoint/nearest/all"

}