package no.uio.ifi.in2000.team8.model.metalerts

import com.google.gson.annotations.SerializedName

data class TimeInterval (

    @SerializedName("interval" ) var interval : ArrayList<String> = arrayListOf()

)