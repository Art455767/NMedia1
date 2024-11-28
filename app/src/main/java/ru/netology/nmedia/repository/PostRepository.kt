package ru.netology.nmedia.repository
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun likeByID(id: Long, likedByMy: Boolean)
    fun shareByID(id: Long)
    fun removeByID(id: Long)
    fun save(post: Post)
}