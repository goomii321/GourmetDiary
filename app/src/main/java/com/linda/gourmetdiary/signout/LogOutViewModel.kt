package com.linda.gourmetdiary.signout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linda.gourmetdiary.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class LogOutViewModel :ViewModel() {

    private val _leave = MutableLiveData<Boolean>()
    val leave: LiveData<Boolean>
        get() = _leave

    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    private var viewModelJob = Job()

    fun leave(needRefresh: Boolean = false) {
        _leave.value = needRefresh
    }
    fun onLeft() {
        _leave.value = null
    }
}