package com.linda.gourmetdiary.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linda.gourmetdiary.network.LoadApiStatus
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.data.Result
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

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

    private var _diary = MutableLiveData<List<Diary>>()
    val diary: LiveData<List<Diary>>
        get() = _diary

    private var _diaryDaily = MutableLiveData<List<Diary>>()
    val diaryDaily: LiveData<List<Diary>>
        get() = _diaryDaily

    private var sameStore = mutableListOf<String>()
    var sameStoreStatus = MutableLiveData<Boolean>()

    private val _navigateToDetail = MutableLiveData<Diary>()
    val navigateToDetail: LiveData<Diary>
        get() = _navigateToDetail

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val isLoggedIn
        get() = UserManager.isLoggedIn

    var score = 0F
    var listSize = 1F
    var scoreAverage = 0F

    var count = MutableLiveData<Int>().apply { value = 2 }
    var listStore =MutableLiveData<String>()
    var countText = MutableLiveData<String>()
    var listStoreText = MutableLiveData<String>()
    val healthyScore = MutableLiveData<String>()
    val healthyScoreText = MutableLiveData<String>()
    var healthyScoreStatus = MutableLiveData<Boolean>()

    var timeNow = LocalTime.now()
    var timeMorning = LocalTime.parse(timeMorningString)
    var timeNoon = LocalTime.parse(timeNoonString)
    var timeAfternoon = LocalTime.parse(timeAfternoonString)
    var timeNight = LocalTime.parse(timeNightString)
    var timeMidnight = LocalTime.parse(timeMidnightString)
    var greetingStatus = MutableLiveData<Int>()

    var dayTime :Long = TimeConverters.dateToTimestamp(LocalDate.now().toString(), Locale.TAIWAN)

    init {
        getTime2SetGreeting()
        getDiaries(getStartTime(),getEndTime())
        getHomeList(dayTime,getEndTime())
    }

    private fun getTime2SetGreeting(){
        _status.value = LoadApiStatus.LOADING
        if (timeNow.isAfter(timeMorning) && timeNow.isBefore(timeNoon)){
            greetingStatus.value = -1
            _status.value = LoadApiStatus.DONE
        } else if (timeNow.isAfter(timeNoon) && timeNow.isBefore(timeAfternoon)) {
            greetingStatus.value = -2
            _status.value = LoadApiStatus.DONE
        } else if (timeNow.isAfter(timeAfternoon) && timeNow.isBefore(timeNight)) {
            greetingStatus.value = -3
            _status.value = LoadApiStatus.DONE
        } else if (timeNow.isAfter(timeNight) && timeNow.isBefore(timeMidnight)) {
            greetingStatus.value = -4
            _status.value = LoadApiStatus.DONE
        } else {
            greetingStatus.value = -5
            _status.value = LoadApiStatus.DONE
        }
    }

    private fun getDiaries(startTime: Long, endTime: Long) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getDiaries(startTime, endTime)

            _diary.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
//                    Log.i("getUsersResult","getUsersResult = ${result.data}")
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
            getSameStore(diary.value!!)
            getHealthy()
        }
    }

    private fun getHomeList(startTime: Long, endTime: Long) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getDiaries(startTime, endTime)

            _diaryDaily.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
//                    Log.i("getHomeList","getHomeList = ${result.data}")
                    result.data.sortedByDescending { it.eatingTime }
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

    private fun getStartTime(): Long =
        TimeConverters.dateToTimestamp(LocalDate.now().toString(), Locale.TAIWAN).minus(timestampOfWeek)

    //get LocaleDate's 00:00 and plus into 23:59
    private fun getEndTime(): Long =
        TimeConverters.dateToTimestamp(LocalDate.now().toString(), Locale.TAIWAN).plus(timestampOfDay)

    private fun getSameStore(diaries: List<Diary>){

        diaries.forEach { item ->
            item.store?.storeName.let {
                if (it != null) {
                    sameStore.add(it)
                }
            }
        }

        for ( item in sameStore ){
            val test = sameStore.filter { it == item }
            if (test.count() > count.value ?: 2){
                count.value = test.count()
                listStore.value = test.first()
                sameStoreStatus.value = true
            }
        }
    }

    private fun getHealthy(){
        diary.value?.forEach {
            it.food?.healthyScore.let {number ->
                if (number != null) {
                    score += (number.toInt())
                }
            }
        }
        listSize = diary.value?.size?.toFloat() ?: 1F
        if ( listSize != 0F) {
            scoreAverage = score/listSize
            healthyScore.value = BigDecimal(scoreAverage.toString()).setScale(1, RoundingMode.HALF_DOWN).toString()
        }
    }

    fun clearReminder(){
        count.value = null
        listStore.value = null
        countText.value = null
        listStoreText.value = null
        healthyScore.value = null
        healthyScoreText.value = null
    }

    fun navigateToDetail(diary: Diary) {
        _navigateToDetail.value = diary
    }

    fun onDetailNavigated() {
        _navigateToDetail.value = null
    }
}
