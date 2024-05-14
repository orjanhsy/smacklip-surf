package no.uio.ifi.in2000.team8.model.waveforecast

import com.google.gson.annotations.SerializedName

data class TimeStamps (
    @SerializedName("available_forecast_times" ) var availableForecastTimes : ArrayList<String> = arrayListOf()
)