package surik.simyan.locdots.server.data

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bson.Document
import org.bson.types.ObjectId
import surik.simyan.locdots.server.utils.ObjectIdAsStringSerializer

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Dot(
    @Serializable(with = ObjectIdAsStringSerializer::class)
    @SerialName("_id")
    @Contextual
    val id: ObjectId,
    val userId: String,
    val message: String,
    val location: Coordinates,
    @Contextual
    val dateTime: LocalDateTime
) {
    fun toDocument(): Document {
        val geoJsonLocation =
            Document("type", "Point").append("coordinates", listOf(location.longitude, location.latitude))

        return Document()
            .append("_id", id)
            .append("userId", userId)
            .append("message", message)
            .append("location", geoJsonLocation)
            .append("dateTime", dateTime.toString())
    }

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun fromDocument(document: Document): Dot {
            val id = document.getObjectId("_id")
            val userId = document.getString("userId")
            val message = document.getString("message")

            val locationDoc = document.get("location", Document::class.java)
            val coordinatesList = locationDoc.get("coordinates", List::class.java)
            val longitude = (coordinatesList[0] as Number).toDouble()
            val latitude = (coordinatesList[1] as Number).toDouble()
            val coordinates = Coordinates(latitude, longitude)

            val dateTimeString = document.getString("dateTime")
            val dateTime = LocalDateTime.parse(dateTimeString)

            return Dot(id, userId, message, coordinates, dateTime)
        }
    }
}