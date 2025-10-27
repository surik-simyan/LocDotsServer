package surik.simyan.locdots.server.domain

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.serializers.LocalDateTimeIso8601Serializer
import kotlinx.serialization.Serializable
import surik.simyan.locdots.server.data.Coordinates

@Serializable
data class Dot(
    val id: String,
    val message: String,
    val coordinates: Coordinates,
    @Serializable(with = LocalDateTimeIso8601Serializer::class)
    val dateTime: LocalDateTime,
    val distance: Double? = null,
)
