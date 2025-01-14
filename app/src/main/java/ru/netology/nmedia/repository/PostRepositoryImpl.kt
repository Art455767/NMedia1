package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit


class PostRepositoryImpl: PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }


    override fun getAllAsync(callback: PostRepository.PostCallback<List<Post>>) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body
                    if (body == null) {
                        callback.onError(RuntimeException("body is null"))
                        return
                    }
                    try {
                        callback.onSuccess(gson.fromJson<List<Post>>(body.string(), typeToken.type))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }


                }

            })
    }


    override fun likeByID(
        id: Long,
        likedByMe: Boolean,
        callback: PostRepository.PostCallback<Post>
    ) {
        val requestBuilder = Request.Builder()
            .url("${BASE_URL}/api/slow/posts/$id/likes")
            .apply {
                if (likedByMe) delete() else post("{}".toRequestBody("application/json".toMediaType()))
            }


        client.newCall(requestBuilder.build())

            .enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body
                        if (body == null) {
                            callback.onError(RuntimeException("body is null"))
                            return
                        }
                        try {
                            callback.onSuccess(gson.fromJson<Post>(body.string(), Post::class.java))
                        } catch (e: Exception) {
                            callback.onError(e)
                        }

                    }
                }
            )

    }

    override fun shareByID(id: Long) {

    }

    override fun save(post: Post,  callback: PostRepository.PostCallback<Post>) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(
                object : Callback{
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body
                        if (body == null) {
                            callback.onError(RuntimeException("body is null"))
                            return
                        }
                        try {
                            callback.onSuccess(gson.fromJson<Post>(body.string(), Post::class.java))
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    }

                }
            )
    }

    override fun removeByID(id: Long, callback: PostRepository.PostCallback<Unit>) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .enqueue(
                object: Callback{
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body
                        if (body == null) {
                            callback.onError(RuntimeException("body is null"))
                            return
                        }
                        try {
                            callback.onSuccess(Unit)
                    }catch (e: Exception){
                        callback.onError(e)

                        }                    }
                }
            )
    }
}
