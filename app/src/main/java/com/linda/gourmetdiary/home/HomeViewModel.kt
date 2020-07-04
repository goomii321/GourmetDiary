package com.linda.gourmetdiary.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.appworks.school.publisher.network.LoadApiStatus
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.data.Users
import com.linda.gourmetdiary.data.Result
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: DiaryRepository) : ViewModel() {

    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _refreshStatus = MutableLiveData<Boolean>()
    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

//    private var _users = MutableLiveData<List<Users>>()
//    val users: LiveData<List<Users>>
//        get() = _users

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
//        Logger.i("------------------------------------")
//        Logger.i("[${this::class.simpleName}]${this}")
//        Logger.i("------------------------------------")

//        if (DiaryApplication.instance.isLiveDataDesign()) {
//            getLiveArticlesResult()
//        } else {
//            getArticlesResult()
//        }
//        getUsersResult()
    }

//    fun getUsersResult() {
//
//        coroutineScope.launch {
//
//            _status.value = LoadApiStatus.LOADING
//
//            val result = repository.getUsers()
//
//            _users.value = when (result) {
//                is Result.Success -> {
//                    _error.value = null
//                    _status.value = LoadApiStatus.DONE
//                    result.data
//                }
//                is Result.Fail -> {
//                    _error.value = result.error
//                    _status.value = LoadApiStatus.ERROR
//                    null
//                }
//                is Result.Error -> {
//                    _error.value = result.exception.toString()
//                    _status.value = LoadApiStatus.ERROR
//                    null
//                }
//                else -> {
//                    _error.value = DiaryApplication.instance.getString(R.string.ng_msg)
//                    _status.value = LoadApiStatus.ERROR
//                    null
//                }
//            }
//            _refreshStatus.value = false
//        }
//    }
}
