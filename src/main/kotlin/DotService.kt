package surik.simyan.locdots

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Indexes
import com.mongodb.client.model.geojson.Point
import com.mongodb.client.model.geojson.Position
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.bson.Document
import org.bson.types.ObjectId
import surik.simyan.locdots.server.data.Coordinates
import surik.simyan.locdots.server.data.Dot
import surik.simyan.locdots.server.data.Payload

class DotService(private val database: MongoDatabase) {
    var collection: MongoCollection<Document>

    init {
        collection = database.getCollection("dots")
    }

    suspend fun ensureIndexesAndCollections() {
        withContext(Dispatchers.IO) {
            try {
                database.createCollection("dots")
            } catch (e: Exception) {
                println("Collection 'dots' might already exist or an error occurred during creation: ${e.message}")
            }
            collection.createIndex(Indexes.geo2dsphere("location"))
        }
    }

    // Create new dot
    suspend fun create(payload: Payload): String = withContext(Dispatchers.IO) {
        val doc = Dot(
            ObjectId(),
            payload.userId!!,
            payload.message!!,
            payload.coordinates!!,
            Clock.System.now().toLocalDateTime(TimeZone.UTC)
        ).toDocument()
        collection.insertOne(doc)
        doc["_id"].toString()
    }

    // Read all dots
    suspend fun read(
        userLatitude: Double,
        userLongitude: Double,
    ): List<Dot>? = withContext(Dispatchers.IO) {
        val userLocation = Point(Position(userLongitude, userLatitude))
        val maxDistanceMeters = 5 * 1000.0
        val pipeline = listOf(
            Document(
                "\$geoNear", Document()
                    .append("near", userLocation)
                    .append("distanceField", "dist.calculated")
                    .append("maxDistance", maxDistanceMeters)
                    .append("spherical", true)
            )
        )
        collection.aggregate(pipeline, Document::class.java)
            .map { doc -> Dot.fromDocument(doc) }
            .toList()
    }

    // Read a dot
    suspend fun read(id: String): Dot? = withContext(Dispatchers.IO) {
        collection.find(Filters.eq("_id", ObjectId(id))).first()?.let(Dot::fromDocument)
    }

    // Update a dot
    suspend fun update(id: String, dot: Dot): Document? = withContext(Dispatchers.IO) {
        collection.findOneAndReplace(Filters.eq("_id", ObjectId(id)), dot.toDocument())
    }

    // Delete a dot
    suspend fun delete(id: String): Document? = withContext(Dispatchers.IO) {
        collection.findOneAndDelete(Filters.eq("_id", ObjectId(id)))
    }
}

