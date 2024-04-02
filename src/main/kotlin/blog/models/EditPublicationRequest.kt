package blog.models

import kotlinx.serialization.Serializable

@Serializable
data class EditPublicationRequest(val body: String)
