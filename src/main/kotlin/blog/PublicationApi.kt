import blog.models.CreatePublicationRequest
import blog.models.EditPublicationRequest
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import blog.storage.IPublicationsStorage

fun Route.publicationApi(storage: IPublicationsStorage) {
    put("/publications") {
        val request = call.receive<CreatePublicationRequest>()
        val result = storage.createPublication(request.body)
        result.fold(
            onSuccess = { publication -> call.respond(publication) },
            onFailure = { ex -> call.respondText(ex.message ?: "Unknown error", status = HttpStatusCode.BadRequest) }
        )
    }

    patch("/publications/{id}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
        val request = call.receive<EditPublicationRequest>()
        val result = storage.editPublication(id, request.body)
        result.fold(
            onSuccess = { publication -> call.respond(publication) },
            onFailure = { ex ->
                when (ex) {
                    is NoSuchElementException -> call.respond(HttpStatusCode.NotFound, ex.message ?: "Publication not found")
                    is IllegalArgumentException -> call.respond(HttpStatusCode.BadRequest, ex.message ?: "Invalid request")
                    else -> call.respond(HttpStatusCode.InternalServerError, "An unexpected error occurred")
                }
            }
        )
    }

    get("/publications/{id}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
        val result = storage.getPublication(id)
        result.fold(
            onSuccess = { publication -> call.respond(publication) },
            onFailure = { ex -> call.respond(HttpStatusCode.NotFound, ex.message ?: "Publication not found") }
        )
    }

    get("/publications") {
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val pageSize = call.request.queryParameters["size"]?.toIntOrNull() ?: 10
        val result = storage.getAllPublications(page, pageSize)
        result.fold(
            onSuccess = { publications -> call.respond(publications) },
            onFailure = { ex -> call.respond(HttpStatusCode.BadRequest, ex.message ?: "Invalid parameters") }
        )
    }

    delete("/publications/{id}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
        val result = storage.deletePublication(id)
        result.fold(
            onSuccess = { call.respond(HttpStatusCode.OK) },
            onFailure = { ex -> call.respond(HttpStatusCode.NotFound, ex.message ?: "Publication not found") }
        )
    }
}