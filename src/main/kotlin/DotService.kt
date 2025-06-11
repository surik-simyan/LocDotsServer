package surik.simyan.locdots

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.bson.Document
import org.bson.types.ObjectId
import surik.simyan.locdots.server.data.Dot
import surik.simyan.locdots.server.data.Payload

class DotService(private val database: MongoDatabase) {
    var collection: MongoCollection<Document>

    init {
        database.createCollection("dots")
        collection = database.getCollection("dots")
    }

    // Create new dot
    suspend fun create(payload: Payload): String = withContext(Dispatchers.IO) {
        val doc = Dot(
            ObjectId(),
            payload.userId!!,
            payload.message!!,
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        ).toDocument()
        collection.insertOne(doc)
        doc["_id"].toString()
    }

    // Read all dots
    suspend fun read(): List<Dot>? = withContext(Dispatchers.IO) {
        collection.find().map { it -> Dot.fromDocument(it) }.toList()
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

