package com.linda.gourmetdiary.stores

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.data.Stores
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.linda.gourmetdiary.data.Result

class StoresViewModel(private val repository: DiaryRepository) : ViewModel() {

    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    var liveStore = MutableLiveData<List<Stores>>()

    private var _store = MutableLiveData<List<Stores>>()
    val store: LiveData<List<Stores>>
        get() = _store

    private val _refreshStatus = MutableLiveData<Boolean>()
    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    private val _navigateToDetail = MutableLiveData<Stores>()
    val navigateToDetail: LiveData<Stores>
        get() = _navigateToDetail

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        if (DiaryApplication.instance.isLiveDataDesign()) {
            getLiveStoreResult()
        } else {
            getStoresResult()
        }
    }
    private fun getStoresResult() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getStore()

            _store.value = when (result) {
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
    fun getLiveStoreResult() {
        liveStore = repository.getLiveStore()
        _status.value = LoadApiStatus.DONE
        _refreshStatus.value = false
    }

    fun refresh() {
        if (DiaryApplication.instance.isLiveDataDesign()) {
            _status.value = LoadApiStatus.DONE
            _refreshStatus.value = false
        } else {
            if (status.value != LoadApiStatus.LOADING) {
                getStoresResult()
            }
        }
    }
    fun navigateToDetail(stores: Stores) {
        _navigateToDetail.value = stores
    }

    fun onDetailNavigated() {
        _navigateToDetail.value = null
    }
}
