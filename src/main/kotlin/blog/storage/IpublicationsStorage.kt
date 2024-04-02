package blog.storage

import blog.models.Publication


interface IPublicationsStorage {
    fun createPublication(body: String): Result<Publication>
    fun editPublication(id: Int, body: String): Result<Publication>
    fun getPublication(id: Int): Result<Publication>
    fun getAllPublications(page: Int, pageSize: Int): Result<List<Publication>>
    fun deletePublication(id: Int): Result<Unit>
}
