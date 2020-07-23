package com.linda.gourmetdiary.template

import android.util.Log
import androidx.databinding.InverseMethod
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

    var year = 0
    var month = 0
    var day = 0
    var hour = 0
    var minute = 0

    var saveYear = MutableLiveData<Int>()
    var saveMonth = MutableLiveData<Int>()
    var saveDay = MutableLiveData<Int>()
    var saveHour = MutableLiveData<Int>()
    var saveMinute = MutableLiveData<Int>()

    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _invalidCheckout = MutableLiveData<Int>()
    val invalidCheckout: LiveData<Int>
        get() = _invalidCheckout

    val recyclerViewStarus = MutableLiveData<Boolean>()

    private var _diary = MutableLiveData<List<Diary>>()
    val diary: LiveData<List<Diary>>
        get() = _diary

    private var _searchDiary = MutableLiveData<Diary>()
    val searchDiary: LiveData<Diary>
        get() = _searchDiary

    private val _editDiary = MutableLiveData<Diary>().apply {
        value = Diary( food = Food(), store = Store())
    }
    val editDiary: LiveData<Diary>
        get() = _editDiary

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
//                    Log.i("diary", "Result.Success diary = ${result.data}")
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

    fun addData(diarys: Diary) {

        when {
            editDiary.value?.food?.foodName.isNullOrEmpty() -> _invalidCheckout.value = -1
            editDiary.value?.eatingTime == null -> _invalidCheckout.value = -2
            editDiary.value?.store?.storeName.isNullOrEmpty() -> _invalidCheckout.value = -3
            else -> coroutineScope.launch {

                _status.value = LoadApiStatus.LOADING

                when (val result = repository.postDiary(diarys)) {
                    is Result.Success -> {
                        _error.value = null
                        _status.value = LoadApiStatus.DONE
                    }
                    is Result.Fail -> {
                        _error.value = result.error
                        _status.value = LoadApiStatus.ERROR
                    }
                    is Result.Error -> {
                        _error.value = result.exception.toString()
                        _status.value = LoadApiStatus.ERROR
                    }
                    else -> {
                        _error.value = DiaryApplication.instance.getString(R.string.ng_msg)
                        _status.value = LoadApiStatus.ERROR
                    }
                }
            }
        }
    }

    @InverseMethod("convertIntToString")
    fun convertStringToInt(value: String): Int {
        return try {
            value.toInt().let {
                when (it) {
                    0 -> 0
                    else -> it
                }
            }
        } catch (e: NumberFormatException) {
            1
        }
    }

    fun convertIntToString(value: Int): String {
        return value.toString()
    }
}
