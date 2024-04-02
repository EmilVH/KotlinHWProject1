package blog.models

import kotlinx.serialization.Serializable

@Serializable
data class CreatePublicationRequest(val body: String)
