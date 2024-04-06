package com.example.myapplication.model.waveforecast

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class PointForecast (

    @SerializedName("idNumber"                ) var idNumber                : Int?    = null,
    @SerializedName("modelName"               ) var modelName               : String? = null,
    @SerializedName("forcastDateTime"         ) var forcastDateTime         : String? = null,
    @SerializedName("modelRunDateTime"        ) var modelRunDateTime        : String? = null,
    @SerializedName("expectedUpdateDatetimes" ) var expectedUpdateDatetimes : String? = null,
    @SerializedName("lastUpdatedDateTime"     ) var lastUpdatedDateTime     : String? = null,
    @SerializedName("tpLocal"                 ) var tpLocal                 : Double? = null,
    @SerializedName("dirLocal"                ) var dirLocal                : Int?    = null,
    @SerializedName("hMaxLocal"               ) var hMaxLocal               : Double? = null,
    @SerializedName("hsLocal"                 ) var hsLocal                 : Double? = null,
    @SerializedName("lat"                     ) var lat                     : Double? = null,
    @SerializedName("lon"                     ) var lon                     : Double? = null
)