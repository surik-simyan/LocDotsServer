package surik.simyan.locdots

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import surik.simyan.locdots.server.data.Payload
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
        // Create dot
        post("/dots") {
            val payload = call.receive<Payload>()
            when {
                payload.message == null -> {
                    call.respond(HttpStatusCode.BadRequest, "Note can not be empty.")
                    return@post
                }

                payload.message.length >= 500 -> {
                    call.respond(HttpStatusCode.BadRequest, "Your note is too long (max 500 characters).")
                    return@post
                }

                payload.userId.isNullOrEmpty() -> {
                    call.respond(HttpStatusCode.BadRequest, "Invalid userId.")
                    return@post
                }

                payload.coordinates == null -> {
                    call.respond(HttpStatusCode.BadRequest, "Invalid coordinates.")
                    return@post
                }

                !isValidLatitude(payload.coordinates.latitude) -> {
                    call.respond(HttpStatusCode.BadRequest, "Invalid latitude. Must be between -90 and 90.")
                    return@post
                }

                !isValidLongitude(payload.coordinates.longitude) -> {
                    call.respond(HttpStatusCode.BadRequest, "Invalid longitude. Must be between -180 and 180.")
                    return@post
                }
            }
            val id = dotService.create(payload)
            call.respond(HttpStatusCode.Created, id)
        }

        // Create dot
        get("/dots") {
            val latitude = call.request.queryParameters["latitude"]?.toDoubleOrNull()
            val longitude = call.request.queryParameters["longitude"]?.toDoubleOrNull()

            if (latitude == null || longitude == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing 'latitude' or 'longitude' query parameters.")
                return@get
            }
            if (!isValidLatitude(latitude)) {
                call.respond(HttpStatusCode.BadRequest, "Invalid latitude. Must be between -90 and 90.")
                return@get
            }
            if (!isValidLongitude(longitude)) {
                call.respond(HttpStatusCode.BadRequest, "Invalid longitude. Must be between -180 and 180.")
                return@get
            }

            val dots = dotService.read(latitude, longitude).toDomain()
            call.respond(HttpStatusCode.OK, dots)
        }
//        // Read car
//        get("/cars/{id}") {
//            val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
//            carService.read(id)?.let { car ->
//                call.respond(car)
//            } ?: call.respond(HttpStatusCode.NotFound)
//        }
//        // Update car
//        put("/cars/{id}") {
//            val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
//            val car = call.receive<Car>()
//            carService.update(id, car)?.let {
//                call.respond(HttpStatusCode.OK)
//            } ?: call.respond(HttpStatusCode.NotFound)
//        }
//        // Delete car
//        delete("/cars/{id}") {
//            val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
//            carService.delete(id)?.let {
//                call.respond(HttpStatusCode.OK)
//            } ?: call.respond(HttpStatusCode.NotFound)
//        }
    }
}

/**
 * Establishes connection with a MongoDB database.
 *
 * The following configuration properties (in application.yaml/application.conf) can be specified:
 * * `db.mongo.maxPoolSize` maximum number of connections to a MongoDB server
 * * `db.mongo.database.name` name of the database
 *
 * IMPORTANT NOTE: in order to make MongoDB connection working, you have to start a MongoDB server first.
 * See the instructions here: https://www.mongodb.com/docs/manual/administration/install-community/
 * all the paramaters above
 *
 * @returns [MongoDatabase] instance
 * */
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
