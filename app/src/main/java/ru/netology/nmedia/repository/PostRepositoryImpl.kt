package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit

class PostRepositoryImpl(
) : PostRepository {
    private val client = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).build()

    private val gson = Gson()

    private val typeToken = object : TypeToken<List<Post>>() {}

    private companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }


    override fun getAll(): List<Post> {
        val request = Request.Builder()
            .url("${BASE_URL}api/slow/posts")
            .build()

        return client.newCall(request).execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }.let {
                gson.fromJson(it, typeToken.type)
            }
    }

    override fun save(post: Post): Post {
        val request = Request.Builder()
            .url("${BASE_URL}api/slow/posts")
            .post(gson.toJson(post).toRequestBody(jsonType)).
            build()

        val responseBody = client.newCall(request).execute().body?.string()
            ?: throw RuntimeException("Response body is null")

        return gson.fromJson(responseBody, Post::class.java)

    }

    override fun likeByID(id: Long){

    }

    override fun shareByID(id: Long){

    }

    override fun removeByID(id: Long){
        val request = Request.Builder()
            .url("${BASE_URL}api/slow/posts/${id}")
            .delete()
            .build()

        client.newCall(request).execute()


    }
}