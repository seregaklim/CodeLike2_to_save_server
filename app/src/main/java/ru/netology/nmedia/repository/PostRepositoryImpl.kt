package ru.netology.nmedia.repository

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okio.IOException
import ru.netology.nmedia.api.*
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError

class PostRepositoryImpl(private val dao: PostDao) : PostRepository {

    override val data = dao.getAll()
        .map(List<PostEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override suspend fun getAll() {
        try {
            val response = PostsApi.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
        } catch (e: java.io.IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    //метод  который возвращает все посты новее определённого:
    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000L)
            val response = PostsApi.service.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
            emit(body.size)
        }

    }//можем использовать catch в нашем flow и избавиться от отдельной обработки
        // CancellationException, так как catch уже знает о нём
        .catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)


    override suspend fun save(post: Post) {
        try {
            val response = PostsApi.service.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            val response = PostsApi.service.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            dao.removeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(id: Long) {
        try {
            val response = PostsApi.service.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.likeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }


    override suspend fun unlikeById(id: Long) {
        try {
            val response = PostsApi.service.unlikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())

            //код одинаковый с лайк, коды лайк и дизлайк объединены , так как запрос в sql один
            dao.likeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun countMessegePost() {
        try {
            dao.countMessegePost()
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun   unCountNewer(){
        try {
            dao.unCountNewer()
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}










//class PostRepositoryImpl : PostRepository {
//
//    override fun getAllAsync(callback: PostRepository.Callback<List<Post>>) {
//        PostsApi.retrofitService.getAll().enqueue(object : Callback<List<Post>> {
//            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
//                if (!response.isSuccessful) {
//                    callback.onError(RuntimeException(response.message()))
//                    return
//                }
//
//                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
//            }
//
//            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
//              TODO("Not yet implemented")
//
//            }
//        })
//    }
//
//    override fun saveAsync(post: Post, callback: PostRepository.Callback<Post>) {
//
//        PostsApi.retrofitService.save(post).enqueue(object : Callback<Post> {
//            override fun onResponse(call: Call<Post>, response: Response<Post>) {
//                if (!response.isSuccessful) {
//                    callback.onError(RuntimeException(response.message()))
//                    return
//                }
//
//                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
//            }
//
//            override fun onFailure(call: Call<Post>, t: Throwable) {
//               TODO("Not yet implemented")
//
//            }
//        })
//    }
//
//    override fun removeByIdAsync(id: Long, callback: PostRepository.Callback<Unit>) {
//        PostsApi.retrofitService.removeById(id).enqueue(object : Callback<Unit> {
//            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
//                if (!response.isSuccessful) {
//                    callback.onError(RuntimeException(response.message()))
//                    return
//                }
//
//                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
//            }
//
//            override fun onFailure(call: Call<Unit>, t: Throwable) {
//               TODO("Not yet implemented")
//
//            }
//        })
//    }
//
//    override fun likeByIdASync(id: Long, callback: PostRepository.Callback<Post>) {
//        PostsApi.retrofitService.likeById(id).enqueue(object : Callback<Post> {
//            override fun onResponse(call: Call<Post>, response: Response<Post>) {
//                if (!response.isSuccessful) {
//                    callback.onError(RuntimeException(response.message()))
//                    return
//                }
//                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
//
//            }
//
//            override fun onFailure(call: Call<Post>, t: Throwable) {
//                TODO("Not yet implemented")
//
//
//            }
//        })
//    }
//
//    override fun unlikeByIdAsync(id: Long, callback: PostRepository.Callback<Post>) {
//        PostsApi.retrofitService.unlikeById(id).enqueue(object : Callback<Post> {
//            override fun onResponse(call: Call<Post>, response: Response<Post>) {
//                if (!response.isSuccessful) {
//                    callback.onError(RuntimeException(response.message()))
//                    return
//                }
//
//                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
//            }
//
//            override fun onFailure(call: Call<Post>, t: Throwable) {
//                TODO("Not yet implemented")
//            }
//        })
//
//
//    }
//}




