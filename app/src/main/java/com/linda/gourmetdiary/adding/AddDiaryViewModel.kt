package com.linda.gourmetdiary.adding

import android.net.Uri
import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linda.gourmetdiary.network.LoadApiStatus
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.data.*
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.util.Logger
import com.linda.gourmetdiary.util.TimeConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class AddDiaryViewModel(private val repository: DiaryRepository) : ViewModel() {

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

    private val _diary = MutableLiveData<Diary>().apply {
        value = Diary( food = Food(), store = Store())
    }
    val diary: LiveData<Diary>
        get() = _diary

    val images = MutableLiveData<MutableList<String>>().apply {
        value = mutableListOf()
    }

    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    val updateImageStatus = MutableLiveData<Boolean>()

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    var foodStatus = MutableLiveData<Boolean>().apply {
        value = false
    }

    var storeStatus = MutableLiveData<Boolean>().apply {
        value = false
    }

    val foodRating = MutableLiveData<Int>()
    val healthyScore = MutableLiveData<Int>()

    private val _invalidCheckout = MutableLiveData<Int>()
    val invalidCheckout: LiveData<Int>
        get() = _invalidCheckout

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun setFoodVisible() {
        foodStatus.value = foodStatus.value?.not()
    }

    fun setStoreVisible() {
        storeStatus.value = storeStatus.value?.not()
    }

    fun addData(diaries: Diary) {

        when {
            diary.value?.food?.foodName.isNullOrEmpty() -> _invalidCheckout.value = -1
            diary.value?.eatingTime?.toString() == "0" -> _invalidCheckout.value = -2
            diary.value?.store?.storeName.isNullOrEmpty() -> _invalidCheckout.value = -3
            diary.value?.mainImage == "" -> _invalidCheckout.value = -4
            updateImageStatus.value == false -> _invalidCheckout.value = -5
            else -> coroutineScope.launch {

                _status.value = LoadApiStatus.LOADING

                when (val result = repository.postDiary(diaries)) {
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

    fun uploadImage(uri: Uri) {

        coroutineScope.launch {

            updateImageStatus.value = false

            val result = repository.uploadImage(uri)

            _diary.value?.mainImage = when (result) {
                is Result.Success -> {
                    _error.value = null
                    updateImageStatus.value = true
                    images.value?.add(result.data)
                    images.value = images.value
                    _diary.value?.images = images.value
                    Logger.i("diary images = ${_diary.value?.images}")
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    updateImageStatus.value = true
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    updateImageStatus.value = true
                    null
                }
                else -> {
                    _error.value = DiaryApplication.instance.getString(R.string.ng_msg)
                    updateImageStatus.value = true
                    null
                }
            }
        }
    }


    val nowTime = MediatorLiveData<String>().apply {
        addSource(saveYear) {
            it?.let {
                value = calculateNowTime()
            }
        }
        addSource(saveMonth) {
            it?.let {
                value = calculateNowTime()
            }
        }
        addSource(saveDay) {
            it?.let {
                value = calculateNowTime()
            }
        }
        addSource(saveHour) {
            it?.let {
                value = calculateNowTime()
            }
        }
        addSource(saveMinute) {
            it?.let {
                value = calculateNowTime()
            }
        }
    }

    private fun calculateNowTime(): String {
        var time = "${saveYear.value}/${saveMonth.value}/${saveDay.value} " +
                "${saveHour.value}:${saveMinute.value}"

        if ( saveYear.value != null && saveMonth.value != null && saveDay.value != null &&
            saveHour.value !=null && saveMinute.value !=null ){
            saveMinute.value?.let {
                if (it < 10) {
                    time = "${saveYear.value}/${saveMonth.value}/${saveDay.value} " +
                            "${saveHour.value}:0${saveMinute.value}"
                }
            }
            diary.value?.eatingTime = TimeConverters.timeToTimestamp(time, Locale.TAIWAN)
        }
        return time
    }

}