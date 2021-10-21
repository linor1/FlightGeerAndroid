package com.example.android.roomwordssample
import com.squareup.moshi.Json

data class Command(
        @Json(name = "aileron") val aileron: Double?,
        @Json(name = "rudder") val rudder: Double,
        @Json(name = "elevator") val elevator: Double?,
        @Json(name = "throttle") val throttle: Double
)
