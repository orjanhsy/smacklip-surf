package no.uio.ifi.in2000.team8.utils

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


    //WaveForecast
    const val WF_BASE_URL = "https://www.barentswatch.no/bwapi/"
    const val WF_ACCESS_TOKEN_URL = "https://id.barentswatch.no/connect/token"

    //--------paths for WF--------
    // params: x, y (lon, lat). Returns: nearest pointForecast for given (x, y)
    const val WF_CLOSEST_ALL_TIME_URL = "v1/waveforecastpoint/nearest/all"

}