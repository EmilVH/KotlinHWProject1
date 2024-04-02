import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import blog.storage.impl.DefaultPublicationsStorage

fun main() {
    embeddedServer(Netty, port = 8080) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    val storage = DefaultPublicationsStorage()

    routing {
        publicationApi(storage)
    }
}
