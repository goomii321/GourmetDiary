package com.linda.gourmetdiary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.data.Food
import com.linda.gourmetdiary.data.Store
import com.linda.gourmetdiary.data.Users
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.network.LoadApiStatus
import com.linda.gourmetdiary.util.CurrentFragmentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.linda.gourmetdiary.data.Result

class MainViewModel(private val repository: DiaryRepository): ViewModel() {

    val currentFragmentType = MutableLiveData<CurrentFragmentType>()

    private val _refresh = MutableLiveData<Boolean>()
    val refresh: LiveData<Boolean>
        get() = _refresh

    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _user = MutableLiveData<Users>().apply {
        value = Users(
            diarys = Diary(food = Food(), store = Store())
        )
    }
    val user: LiveData<Users>
        get() = _user

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun refresh() {
        if (!DiaryApplication.instance.isLiveDataDesign()) {
            _refresh.value = true
        }
    }

    fun onRefreshed() {
        if (!DiaryApplication.instance.isLiveDataDesign()) {
            _refresh.value = null
        }
    }

    fun getProfile(user: Users) {
        _user.value = _user.value
        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.pushProfile(user)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    Log.i("getProfile","success")
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    Log.i("getProfile","error")
                }
                else -> {
                    _error.value = DiaryApplication.instance.getString(R.string.ng_msg)
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }
}