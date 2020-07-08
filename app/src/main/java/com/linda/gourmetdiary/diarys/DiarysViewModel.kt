package com.linda.gourmetdiary.diarys

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linda.gourmetdiary.network.LoadApiStatus
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.data.Result
import com.linda.gourmetdiary.data.source.DiaryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DiarysViewModel(private val repository: DiaryRepository) : ViewModel() {

    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    var liveDiary = MutableLiveData<List<Diary>>()

    private var _diary = MutableLiveData<List<Diary>>()
    val diary: LiveData<List<Diary>>
        get() = _diary

    private val _refreshStatus = MutableLiveData<Boolean>()
    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    private val _navigateToDetail = MutableLiveData<Diary>()
    val navigateToDetail: LiveData<Diary>
        get() = _navigateToDetail

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        if (DiaryApplication.instance.isLiveDataDesign()) {
            getLiveDiaryResult()
        } else {
            getUsersResult()
        }
    }

    private fun getUsersResult() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getUsersDiarys()

            _diary.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = DiaryApplication.instance.getString(R.string.ng_msg)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
            _refreshStatus.value = false
        }
    }
    fun refresh() {

        if (DiaryApplication.instance.isLiveDataDesign()) {
            _status.value = LoadApiStatus.DONE
            _refreshStatus.value = false

        } else {
            if (status.value != LoadApiStatus.LOADING) {
                getUsersResult()
            }
        }
    }
    fun getLiveDiaryResult() {
        liveDiary = repository.getLiveDiary()
        _status.value = LoadApiStatus.DONE
        _refreshStatus.value = false
    }

    fun navigateToDetail(diary: Diary) {
        _navigateToDetail.value = diary
    }

    fun onDetailNavigated() {
        _navigateToDetail.value = null
    }
}
