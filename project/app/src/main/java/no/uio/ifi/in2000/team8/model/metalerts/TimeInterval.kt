package com.example.myapplication.model.metalerts

import com.google.gson.annotations.SerializedName

data class TimeInterval (

    @SerializedName("interval" ) var interval : ArrayList<String> = arrayListOf()

)