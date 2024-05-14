package no.uio.ifi.in2000.team8.model.metalerts

import com.google.gson.annotations.SerializedName

data class MetAlerts (

    @SerializedName("features"   ) var features   : ArrayList<Alert> = arrayListOf(),
    @SerializedName("lang"       ) var lang       : String?             = null,
    @SerializedName("lastChange" ) var lastChange : String?             = null,
    @SerializedName("type"       ) var type       : String?             = null

)