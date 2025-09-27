package surik.simyan.locdots.server.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import surik.simyan.locdots.server.api.response.*
import surik.simyan.locdots.server.api.exceptions.*
import java.util.*

fun Application.configureStatusPages() {
    install(StatusPages) {

        exception<ApiException> { call, cause ->
            val traceId = UUID.randomUUID().toString()
            call.application.log.warn("[Trace $traceId] API exception: ${cause.code} - ${cause.message}")

            val status = when (cause) {
                is NotFoundException -> HttpStatusCode.NotFound
                is ValidationException -> HttpStatusCode.BadRequest
                is UnauthorizedException -> HttpStatusCode.Unauthorized
                else -> HttpStatusCode.BadRequest
            }

            call.respond(
                status,
                ApiResponse.Error(
                    error = ErrorDetail(
                        code = cause.code,
                        message = cause.message,
                        traceId = traceId
                    )
                )
            )
        }

        exception<Throwable> { call, cause ->
            val traceId = UUID.randomUUID().toString()
            call.application.log.error("[Trace $traceId] Unhandled exception", cause)

            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse.Error(
                    error = ErrorDetail(
                        code = "INTERNAL_SERVER_ERROR",
                        message = "An unexpected error occurred. Please try again later.",
                        traceId = traceId
                    )
                )
            )
        }
    }
}
