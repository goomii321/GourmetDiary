package com.linda.gourmetdiary.template

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.data.Food
import com.linda.gourmetdiary.data.Result
import com.linda.gourmetdiary.data.Store
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TemplateViewModel(private val repository: DiaryRepository) : ViewModel() {

    private var _diary = MutableLiveData<List<Diary>>()
    val diary: LiveData<List<Diary>>
        get() = _diary

    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _invalidCheckout = MutableLiveData<Int>()
    val invalidCheckout: LiveData<Int>
        get() = _invalidCheckout

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun searchTemplate(searchWord:String) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.searchTemplate(searchWord)

            _diary.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    Log.i("diary", "Result.Success diary = ${result.data}")
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

//    fun addData(diarys: Diary) {
//        when {
//            user.value?.food?.foodName.isNullOrEmpty() -> _invalidCheckout.value = -1
//            user.value?.eatingTime == null -> _invalidCheckout.value = -2
//            user.value?.store?.storeName.isNullOrEmpty() -> _invalidCheckout.value = -3
//            else -> coroutineScope.launch {
//
//                _status.value = LoadApiStatus.LOADING
//
//                when (val result = repository.postDiary(diarys)) {
//                    is Result.Success -> {
//                        _error.value = null
//                        _status.value = LoadApiStatus.DONE
//                    }
//                    is Result.Fail -> {
//                        _error.value = result.error
//                        _status.value = LoadApiStatus.ERROR
//                    }
//                    is Result.Error -> {
//                        _error.value = result.exception.toString()
//                        _status.value = LoadApiStatus.ERROR
//                    }
//                    else -> {
//                        _error.value = DiaryApplication.instance.getString(R.string.ng_msg)
//                        _status.value = LoadApiStatus.ERROR
//                    }
//                }
//            }
//        }
//    }
}
