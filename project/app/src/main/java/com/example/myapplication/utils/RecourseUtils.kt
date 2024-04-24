package com.example.myapplication.utils

import com.example.myapplication.R

class RecourseUtils() {

    //For å bruke denne må du lage en instans av klassen der den skal brukes
    //(klassen RecourseUtils må imorteres slik: import com.example.myapplication.utils.RecourseUtils)
    // deretter kalle <instans>.findWeatherSymbol(<symbol_code>)

    public fun findWeatherSymbol(symbolCode: String): Int {

        return when (symbolCode) {
            "clearsky_day" -> R.drawable.clearsky_day
            "clearsky_night" -> R.drawable.clearsky_night
            "clearsky_polartwilight" -> R.drawable.clearsky_polartwilight
            "fair_day" -> R.drawable.fair_day
            "fair_night" -> R.drawable.fair_night
            "fair_polartwilight" -> R.drawable.fair_polartwilight
            "partlycloudy_day" -> R.drawable.partlycloudy_day
            "partlycloudy_night" -> R.drawable.partlycloudy_night
            "partlycloudy_polartwilight" -> R.drawable.partlycloudy_polartwilight
            "cloudy" -> R.drawable.cloudy
            "rainshowers_day" -> R.drawable.rainshowers_day
            "rainshowers_night" -> R.drawable.rainshowers_night
            "rainshowers_polartwilight" -> R.drawable.rainshowers_polartwilight
            "rainshowersandthunder_day" -> R.drawable.rainshowersandthunder_day
            "rainshowersandthunder_night" -> R.drawable.rainshowersandthunder_night
            "rainshowersandthunder_polartwilight" -> R.drawable.rainshowersandthunder_polartwilight
            "sleetshowers_day" -> R.drawable.sleetshowers_day
            "sleetshowers_night" -> R.drawable.sleetshowers_night
            "sleetshowers_polartwilight" -> R.drawable.sleetshowers_polartwilight
            "snowshowers_day" -> R.drawable.snowshowers_day
            "snowshowers_night" -> R.drawable.sleetshowers_night
            "snowshowers_polartwilight" -> R.drawable.snowshowers_polartwilight
            "rain" -> R.drawable.rain
            "heavyrain" -> R.drawable.heavyrain
            "heavyrainandthunder" -> R.drawable.heavyrainandthunder
            "sleet" -> R.drawable.sleet
            "snow" -> R.drawable.snow
            "snowandthunder" -> R.drawable.snowandthunder
            "fog" -> R.drawable.fog
            "sleetshowersandthunder_day" -> R.drawable.sleetshowersandthunder_day
            "sleetshowersandthunder_night" -> R.drawable.sleetshowersandthunder_night
            "sleetshowersandthunder_polartwilight" -> R.drawable.sleetshowersandthunder_polartwilight
            "snowshowersandthunder_day" -> R.drawable.snowshowersandthunder_day
            "snowshowersandthunder_night" -> R.drawable.snowshowersandthunder_night
            "snowshowersandthunder_polartwilight" -> R.drawable.snowshowersandthunder_polartwilight
            "rainandthunder" -> R.drawable.rainandthunder
            "sleetandthunder" -> R.drawable.sleetandthunder
            "lightrainshowersandthunder_day" -> R.drawable.lightrainshowersandthunder_day
            "lightrainshowersandthunder_night" -> R.drawable.lightrainshowersandthunder_night
            "lightrainshowersandthunder_polartwilight" -> R.drawable.lightrainshowersandthunder_polartwilight
            "heavyrainshowersandthunder_day" -> R.drawable.heavyrainshowersandthunder_day
            "heavyrainshowersandthunder_night" -> R.drawable.heavyrainshowersandthunder_night
            "heavyrainshowersandthunder_polartwilight" -> R.drawable.heavyrainshowersandthunder_polartwilight
            "lightssleetshowersandthunder_day" -> R.drawable.lightssleetshowersandthunder_day
            "lightssleetshowersandthunder_night" -> R.drawable.lightssleetshowersandthunder_night
            "lightssleetshowersandthunder_polartwilight" -> R.drawable.lightssleetshowersandthunder_polartwilight
            "heavysleetshowersandthunder_day" -> R.drawable.heavysleetshowersandthunder_day
            "heavysleetshowersandthunder_night" -> R.drawable.heavysleetshowersandthunder_night
            "heavysleetshowersandthunder_polartwilight" -> R.drawable.heavysleetshowersandthunder_polartwilight
            "lightssnowshowersandthunder_day" -> R.drawable.lightssnowshowersandthunder_day
            "lightssnowshowersandthunder_night" -> R.drawable.lightssnowshowersandthunder_night
            "lightssnowshowersandthunder_polartwilight" -> R.drawable.lightssnowshowersandthunder_polartwilight
            "heavysnowshowersandthunder_day" -> R.drawable.heavysnowshowersandthunder_day
            "heavysnowshowersandthunder_night" -> R.drawable.heavysnowshowersandthunder_night
            "heavysnowshowersandthunder_polartwilight" -> R.drawable.heavysnowshowersandthunder_polartwilight
            "lightrainandthunder" -> R.drawable.lightrainandthunder
            "lightsleetandthunder" -> R.drawable.lightsleetandthunder
            "heavysleetandthunder" -> R.drawable.heavysleetandthunder
            "lightsnowandthunder" -> R.drawable.lightsnowandthunder
            "heavysnowandthunder" -> R.drawable.heavysnowandthunder
            "lightrainshowers_day" -> R.drawable.lightrainshowers_day
            "lightrainshowers_night" -> R.drawable.lightrainshowers_night
            "lightrainshowers_polartwilight" -> R.drawable.lightrainshowers_polartwilight
            "heavyrainshowers_day" -> R.drawable.heavyrainshowers_day
            "heavyrainshowers_night" -> R.drawable.heavyrainshowers_night
            "heavyrainshowers_polartwilight" -> R.drawable.heavyrainshowers_polartwilight
            "lightsleetshowers_day" -> R.drawable.lightsleetshowers_day
            "lightsleetshowers_night" -> R.drawable.lightsleetshowers_night
            "lightsleetshowers_polartwilight" -> R.drawable.lightsleetshowers_polartwilight
            "heavysleetshowers_day" -> R.drawable.heavysleetshowers_day
            "heavysleetshowers_night" -> R.drawable.heavysleetshowers_night
            "heavysleetshowers_polartwilight" -> R.drawable.heavysleetshowers_polartwilight
            "lightsnowshowers_day" -> R.drawable.lightsnowshowers_day
            "lightsnowshowers_night" -> R.drawable.lightsnowshowers_night
            "lightsnowshowers_polartwilight" -> R.drawable.lightsnowshowers_polartwilight
            "heavysnowshowers_day" -> R.drawable.heavysnowshowers_day
            "heavysnowshowers_night" -> R.drawable.heavysnowshowers_night
            "heavysnowshowers_polartwilight" -> R.drawable.heavysnowshowers_polartwilight
            "lightrain" -> R.drawable.lightrain
            "lightsleet" -> R.drawable.lightsleet
            "heavysleet" -> R.drawable.heavysleet
            "lightsnow" -> R.drawable.lightsnow
            "heavysnow" -> R.drawable.heavysnow
            else -> R.drawable.spm //TODO: bytte til termometerikon kanskje?
        }
    }
}