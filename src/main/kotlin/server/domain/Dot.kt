package surik.simyan.locdots.server.domain

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import surik.simyan.locdots.server.data.Coordinates

@Serializable
data class Dot(
    val id: String,
    val message: String,
    val location: Coordinates,
    val dateTime: LocalDateTime,
)