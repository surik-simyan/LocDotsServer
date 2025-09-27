package surik.simyan.locdots

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import surik.simyan.locdots.server.api.response.respondSuccess
import surik.simyan.locdots.server.api.util.requireParameter
import surik.simyan.locdots.server.data.CreateDotBody
import surik.simyan.locdots.server.mappers.toDomain

fun Application.configureDatabases() {
    val mongoDatabase = connectToMongoDB()
    val dotService = DotService(mongoDatabase)

    launch {
        dotService.ensureIndexesAndCollections()
    }

    fun isValidLatitude(lat: Double): Boolean {
        return lat >= -90 && lat <= 90
    }

    fun isValidLongitude(lng: Double): Boolean {
        return lng >= -180 && lng <= 180
    }

    routing {
        // Get dots
        get("/dots") {
            val latitude = call.request.queryParameters["latitude"]?.toDoubleOrNull()
            val longitude = call.request.queryParameters["longitude"]?.toDoubleOrNull()

            requireParameter(latitude != null) { "Missing latitude query parameters." }
            requireParameter(longitude != null) { "Missing longitude query parameters." }
            requireParameter(isValidLatitude(latitude)) { "Invalid latitude. Must be between -90 and 90." }
            requireParameter(isValidLongitude(longitude)) { "Invalid longitude. Must be between -180 and 180." }

            val dots = dotService.read(latitude, longitude).toDomain()
            call.respondSuccess(data = dots)
        }

        // Create dot
        post("/dots") {
            val createDotBody = call.receive<CreateDotBody>()
            requireParameter(createDotBody.message != null) { "Note can not be empty." }
            requireParameter(createDotBody.message.length < 500) { "Your note is too long (max 500 characters)." }
            requireParameter(createDotBody.userId.isNullOrEmpty().not()) { "Invalid userId." }
            requireParameter(createDotBody.coordinates != null) { "Invalid coordinates." }
            requireParameter(isValidLatitude(createDotBody.coordinates.latitude)) { "Invalid latitude. Must be between -90 and 90." }
            requireParameter(isValidLongitude(createDotBody.coordinates.longitude)) { "Invalid longitude. Must be between -180 and 180." }

            val id = dotService.create(createDotBody)
            call.respondSuccess(data = mapOf("id" to id), status = HttpStatusCode.Created)
        }
    }
}

fun Application.connectToMongoDB(): MongoDatabase {
    val maxPoolSize = environment.config.tryGetString("mongo.maxPoolSize")?.toInt() ?: 20
    val databaseName = environment.config.tryGetString("mongo.databaseName") ?: "dots"

    val uri = "mongodb://mongodb:27017/?maxPoolSize=$maxPoolSize&w=majority"

    val mongoClient = MongoClients.create(uri)
    val database = mongoClient.getDatabase(databaseName)

    monitor.subscribe(ApplicationStopped) {
        mongoClient.close()
    }

    return database
}
