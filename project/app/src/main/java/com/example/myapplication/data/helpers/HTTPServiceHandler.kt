package com.example.myapplication.data.helpers

object HTTPServiceHandler {
    //Global API authenticator
    ***REMOVED***
    ***REMOVED***

    //LocationForecast
    const val LOCATION_FORECAST_URL = "https://gw-uio.intark.uh-it.no/in2000/weatherapi/locationforecast/2.0/complete?lat=62.1255693551118&lon=5.152407834229069"

    //MetAlerts
    const val METALERTS_URL = "https://gw-uio.intark.uh-it.no/in2000/weatherapi/weatherapi/metalerts/2.0/current.json"

    //Oceanforecast
    const val OCEAN_FORECAST_URL = "https://gw-uio.intark.uh-it.no/in2000/weatherapi/oceanforecast/2.0/complete?lat=62.1255693551118&lon=5.152407834229069"

    //WaveForecast
    const val WAVE_FORECAST_URL = "https://www.barentswatch.no/bwapi/openapi/waveforecast/openapi.json"
    const val WAVE_FORECAST_POINT_FORECAST = "https://www.barentswatch.no/bwapi/v1/geodata/waveforecast/pointforecast"
}