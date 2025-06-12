package surik.simyan.locdots.server.data

import kotlinx.serialization.Serializable

@Serializable
data class Payload(
    val userId: String?,
    val message: String?,
    val coordinates: Coordinates?
)