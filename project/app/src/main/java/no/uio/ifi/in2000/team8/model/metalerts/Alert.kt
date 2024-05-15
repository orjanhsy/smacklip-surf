package no.uio.ifi.in2000.team8.model.metalerts

import com.google.gson.annotations.SerializedName

data class Alert (

    @SerializedName("geometry"   ) var geometry   : Geometry?   = Geometry(),
    @SerializedName("properties" ) var properties : Properties? = Properties(),
    @SerializedName("type"       ) var type       : String?     = null,
    @SerializedName("when"       ) var timeInterval: TimeInterval? = TimeInterval()

)
