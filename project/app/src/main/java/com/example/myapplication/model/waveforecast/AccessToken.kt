package com.example.myapplication.model.waveforecast

import com.google.gson.annotations.SerializedName

data class AccessToken (
    @SerializedName("access_token")  var accessToken: String,
    @SerializedName("token_type")    var tokenType: String,
    @SerializedName("expires_in")    var expiresIn: Int,
    @SerializedName("refresh_token") var refreshToken: String? = null
)