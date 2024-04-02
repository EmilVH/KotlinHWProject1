package blog.storage.impl

import blog.models.Publication
import blog.storage.IPublicationsStorage
import kotlinx.datetime.Clock
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class DefaultPublicationsStorage : IPublicationsStorage {
    private val publications = ConcurrentHashMap<Int, Publication>()
    private val nextId = AtomicInteger(1)

    override fun createPublication(body: String): Result<Publication> {
        if (body.length > 500) {
            return Result.failure(IllegalArgumentException("Publication body exceeds 500 characters."))
        }
        val id = nextId.getAndIncrement()
        val publication = Publication(id, body, Clock.System.now(), Clock.System.now())
        publications[id] = publication
        return Result.success(publication)
    }

    override fun editPublication(id: Int, body: String): Result<Publication> {
        if (body.length > 500) {
            return Result.failure(IllegalArgumentException("Publication body exceeds 500 characters."))
        }
        val publication = publications[id] ?: return Result.failure(NoSuchElementException("Publication not found."))
        val updatedPublication = publication.copy(body = body, editingTime = Clock.System.now())
        publications[id] = updatedPublication
        return Result.success(updatedPublication)
    }

    override fun getPublication(id: Int): Result<Publication> {
        val publication = publications[id] ?: return Result.failure(NoSuchElementException("Publication not found."))
        return Result.success(publication)
    }

    override fun getAllPublications(page: Int, pageSize: Int): Result<List<Publication>> {
        if (page <= 0 || pageSize <= 0) {
            return Result.failure(IllegalArgumentException("Page and page size must be positive."))
        }
        val startIndex = (page - 1) * pageSize
        val publicationsPage = publications.values.sortedBy { it.creationTime }.drop(startIndex).take(pageSize)
        return Result.success(publicationsPage)
    }

    override fun deletePublication(id: Int): Result<Unit> {
        if (!publications.containsKey(id)) {
            return Result.failure(NoSuchElementException("Publication not found."))
        }
        publications.remove(id)
        return Result.success(Unit)
    }
}