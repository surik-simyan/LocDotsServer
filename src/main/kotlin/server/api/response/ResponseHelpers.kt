package surik.simyan.locdots.server.api.response

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend inline fun <reified T> ApplicationCall.respondSuccess(
    data: T,
    status: HttpStatusCode = HttpStatusCode.OK,
    meta: Meta? = null
) {
    respond(status, ApiResponse.Success(data = data, meta = meta))
}

suspend fun ApplicationCall.respondError(
    status: HttpStatusCode,
    code: String,
    message: String,
    traceId: String? = null
) {
    respond(status, ApiResponse.Error(error = ErrorDetail(code, message, traceId)))
}
