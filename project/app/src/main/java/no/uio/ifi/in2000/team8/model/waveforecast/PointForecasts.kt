package no.uio.ifi.in2000.team8.model.waveforecast

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

import kotlinx.serialization.serializer

data class PointForecasts (
    var pointForecasts: List<PointForecast>
)
