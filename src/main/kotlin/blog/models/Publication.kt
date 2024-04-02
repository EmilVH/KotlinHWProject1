package blog.models
import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant
@Serializable
data class Publication(
    val id: Int,
    var body: String,
    val creationTime: Instant,
    var editingTime: Instant
)
