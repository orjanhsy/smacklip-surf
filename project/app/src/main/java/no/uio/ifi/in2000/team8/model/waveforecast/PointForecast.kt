package no.uio.ifi.in2000.team8.model.waveforecast

import com.google.gson.annotations.SerializedName


data class PointForecast (

    @SerializedName("idNumber"                ) var idNumber                : Int?           = null,
    @SerializedName("modelName"               ) var modelName               : String?           = null,
    @SerializedName("forcastDateTime"         ) var forcastDateTime         : String?           = null,
    @SerializedName("modelRunDateTime"        ) var modelRunDateTime        : String?           = null,
    @SerializedName("expectedUpdateDatetimes" ) var expectedUpdateDatetimes : ArrayList<String> = arrayListOf(),
    @SerializedName("lastUpdatedDateTime"     ) var lastUpdatedDateTime     : String?           = null,
    @SerializedName("tpLocal"                 ) var tpLocal                 : Double?           = null,
    @SerializedName("dirLocal"                ) var dirLocal                : Double?           = null,
    @SerializedName("hMaxLocal"               ) var hMaxLocal               : Double?           = null,
    @SerializedName("hsLocal"                 ) var hsLocal                 : Double?           = null,
    @SerializedName("lat"                     ) var lat                     : Double           = 0.0,
    @SerializedName("lon"                     ) var lon                     : Double          = 0.0

)