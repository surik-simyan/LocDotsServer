package surik.simyan.locdots

import com.mongodb.client.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import surik.simyan.locdots.server.data.Dot
import surik.simyan.locdots.server.data.Payload

fun Application.configureDatabases() {
    val mongoDatabase = connectToMongoDB()
    val dotService = DotService(mongoDatabase)
    routing {
        // Create dot
        post("/dots") {
            val payload = call.receive<Payload>()
            when {
                payload.message == null -> call.respond(HttpStatusCode.BadRequest, "Note can not be empty.")
                payload.message.length >= 500 -> call.respond(HttpStatusCode.BadRequest, "You note is too long.")
                payload.userId.isNullOrEmpty() -> call.respond(HttpStatusCode.BadRequest, "Invalid userId.")
            }
            val id = dotService.create(payload)
            call.respond(HttpStatusCode.Created, id)
        }

        // Create dot
        get("/dots") {
            dotService.read()?.let {
                call.respond(it)
            } ?: call.respond(HttpStatusCode.BadRequest)
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
