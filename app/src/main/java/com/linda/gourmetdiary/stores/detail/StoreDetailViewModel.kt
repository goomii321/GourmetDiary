package com.linda.gourmetdiary.stores.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.data.Stores
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.linda.gourmetdiary.data.Result

class StoreDetailViewModel(private val diaryRepository: DiaryRepository,
                           private val arguments: Stores?) : ViewModel() {
    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _detail = MutableLiveData<Stores>()
    val detail: LiveData<Stores>
        get() = _detail

    private val _store = MutableLiveData<Stores>().apply {
        value = arguments
    }
    val store: LiveData<Stores>
        get() = _store

    private val _history = MutableLiveData<List<Diary>>()
    val history: LiveData<List<Diary>>
        get() = _history

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)
    
    init {
        queryHistory()
    }

    private fun queryHistory() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = diaryRepository.queryStoreHistory()

            _history.value = when (result) {
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
        }
    }
}
