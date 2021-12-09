package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.*
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = "",
    newer=0,
)

class PostViewModel(application: Application) : AndroidViewModel(application) {

    // упрощённый вариант
    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())

    //Для реализации репозитория мы можем добавить явное указание того,
    // какой контекст использовать для работы с помощью flowOn:
    val data: LiveData<FeedModel> = repository.data
        .map(::FeedModel)
        .asLiveData(Dispatchers.Default)

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

//    switchMap позволяет нам подписаться на изменения data и на основании этого получить новую LiveData.
//     Т. е.  «предыдущему» Flow будет отправлен cancel, что приведёт к выбросу CancellationException.
    val newerCount: LiveData<Int> = data.switchMap {
        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
            .catch { e -> e.printStackTrace() }
            .asLiveData(Dispatchers.Default)
    }

    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    // LiveData<ErrorModel> (для обработки ошибки)
    private val _error = SingleLiveEvent<ErrorModel>()
    val error: LiveData<ErrorModel>
        get() = _error


    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _error.postValue(
                ErrorModel(
                    ErrorType.NetworkError, ActionType.GetAll,
                    e.message ?: "",
                )
            )
            // _dataState.value = FeedModelState(error = true)

        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _error.postValue(
                ErrorModel(
                    ErrorType.NetworkError, ActionType.Refresh,
                    e.message ?: "",
                )
            )
            //  _dataState.value = FeedModelState(error = true)
        }
    }

    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    repository.save(it)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _error.postValue(
                        ErrorModel(
                            ErrorType.NetworkError,
                            ActionType.Save, e.message ?: "Не сохранился"
                        )
                    )
                    edited.postValue(empty)

                    // _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }


    fun likeById(id: Long) = viewModelScope.launch {
        try {
            repository.likeById(id)
        } catch (e: Exception) {
            _error.postValue(ErrorModel(ErrorType.AppError, ActionType.Like, e.message ?: ""))
            //_data.postValue(FeedModel(error = true))
        }
    }

    fun unlikeById(id: Long) = viewModelScope.launch {
        try {
            repository.unlikeById(id)


        } catch (e: Exception) {
            _error.postValue(ErrorModel(ErrorType.AppError, ActionType.Like, e.message ?: ""))
            //_data.postValue(FeedModel(error = true))
        }
    }


    fun removeById(id: Long) = viewModelScope.launch {
        try {
            repository.removeById(id)
        } catch (e: Exception) {

            _error.postValue(
                ErrorModel(
                    ErrorType.NetworkError,
                    ActionType.RemoveById, e.message ?: ""
                )
            )

            //_data.postValue(_data.value?.copy(posts = old))
        }

    }

    fun countMessegePost ()= viewModelScope.launch {
            try {
                repository.countMessegePost()
            } catch (e: Exception) {

                _error.postValue(
                    ErrorModel(
                        ErrorType.NetworkError,
                        ActionType.CountMessegePost, e.message ?: ""
                    )
                )

                //_data.postValue(_data.value?.copy(posts = old))
            }
    }

    fun unCountNewer()= viewModelScope.launch {

        try {
            repository.unCountNewer()
        } catch (e: Exception) {

            _error.postValue(
                ErrorModel(
                    ErrorType.NetworkError,
                    ActionType.UnCountMessegePost, e.message ?: ""
                )
            )
            //_data.postValue(_data.value?.copy(posts = old))
        }
    }

}




