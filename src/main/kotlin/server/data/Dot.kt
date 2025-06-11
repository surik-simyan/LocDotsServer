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
    @Contextual
    val dateTime: LocalDateTime
) {
    fun toDocument(): Document = Document.parse(Json.encodeToString(this))

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun fromDocument(document: Document): Dot = json.decodeFromString(document.toJson())
    }
}