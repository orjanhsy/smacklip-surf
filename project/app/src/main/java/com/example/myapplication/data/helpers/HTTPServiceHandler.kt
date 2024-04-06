package com.example.myapplication.data.helpers

object HTTPServiceHandler {
    //Global API authenticator
    ***REMOVED***
    ***REMOVED***

    //LocationForecast
    const val LOCATION_FORECAST_URL = "https://gw-uio.intark.uh-it.no/in2000/weatherapi/locationforecast/2.0/complete"

    //MetAlerts
    const val METALERTS_URL = "https://gw-uio.intark.uh-it.no/in2000/weatherapi/weatherapi/metalerts/2.0/current.json"

    //Oceanforecast
    const val OCEAN_FORECAST_URL = "https://gw-uio.intark.uh-it.no/in2000/weatherapi/oceanforecast/2.0/complete"


    //-----------------------WaveForecast-----------------------
    const val WF_BASE_URL = "https://www.barentswatch.no/bwapi/"
    const val WF_ACCESS_TOKEN_URL = "https://id.barentswatch.no/connect/token"

    //--------paths for WF--------

    //returnerer alle tilgjengelige modeller/fairways (IKKE GJORT SERIALIZABLE) (treig)
    const val WF_AVALIABLE_ALL_URL = "v1/geodata/waveforecast/available/all" // ? NONE

    //returnerer  tilgjengelige forecast-timestamps for en fairway (IKKE GJORT SERIALIZABLE)
    const val WF_AVALIABLE_URL = "/v1/geodata/waveforecast/available" // ?modelname&fairwayid

    //returnerer bolgetilsdtander for et bestemt punkt (IKKE GJORT SERIALIZABLE)
    const val WF_POINT_FORECAST_URL = "v1/geodata/waveforecast/pointforecast" // ?modelname&pointId&time

    //returnerer alle pointforecasts tilgjenglig for et bestemt timestamp, kan hende vi skal bruke det istedefore pointforecast
    const val WF_ALL_POINT_FORECASTS_URL = "v1/geodata/waveforecast/pointforecasts" // ?time


}