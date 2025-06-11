package surik.simyan.locdots

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.bson.types.ObjectId
import surik.simyan.locdots.server.data.Dot

fun Application.configureRouting() {
    routing {
        get("/") {
            val dots = listOf(
                Dot(
                    ObjectId(),
                    "asdasdads",
                    "message 1",
                    Clock.System.now().toLocalDateTime(TimeZone.UTC)
                ),
                Dot(
                    ObjectId(),
                    "asdasdads",
                    "message 2",
                    Clock.System.now().toLocalDateTime(TimeZone.UTC)
                )
            )
            call.respond(dots)
        }
    }
}
