package surik.simyan.locdots.server.api.response

import kotlinx.serialization.Serializable

@Serializable
sealed class ApiResponse<out T> {
    @Serializable
    data class Success<T>(
        val success: Boolean = true,
        val data: T,
        val meta: Meta? = null,
    ) : ApiResponse<T>()

    @Serializable
    data class Error(
        val success: Boolean = false,
        val error: ErrorDetail,
    ) : ApiResponse<Nothing>()
}

@Serializable
data class Meta(
    val page: Int? = null,
    val limit: Int? = null,
    val total: Int? = null,
)

@Serializable
data class ErrorDetail(
    val code: String,
    val message: String,
    val traceId: String? = null,
)
