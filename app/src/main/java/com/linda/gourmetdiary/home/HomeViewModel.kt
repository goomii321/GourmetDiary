package com.linda.gourmetdiary.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linda.gourmetdiary.network.LoadApiStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.data.Result
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.util.Logger
import com.linda.gourmetdiary.util.TimeConverters
import com.linda.gourmetdiary.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import kotlin.system.exitProcess

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

    private var sameStore = mutableListOf<String>()
    var sameStoreStatus = MutableLiveData<Boolean>().apply { value = false }

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val isLoggedIn
        get() = UserManager.isLoggedIn

    var score = 0F
    var listSize = 1F
    var scoreAverage = 0F

    var count = MutableLiveData<Int>().apply { value = 3 }
    var listStore =MutableLiveData<String>()
    var countText = MutableLiveData<String>()
    var listStoreText = MutableLiveData<String>()
    val healthyScore = MutableLiveData<String>()
    val healthyScoreText = MutableLiveData<String>()

    var timeNow = LocalTime.now()
    var timeMorning = LocalTime.parse("05:00:00.00")
    var timeNoon = LocalTime.parse("11:00:00.00")
    var timeAfternoon = LocalTime.parse("14:00:00.00")
    var timeNight = LocalTime.parse("18:00:00.00")
    var timeMidnight = LocalTime.parse("21:00:00.00")
    var helloStatus = MutableLiveData<Int>()

    init {
        helloWorld()
        getUsersResult(getStartTime(),getEndTime())
    }

    fun helloWorld(){
        _status.value = LoadApiStatus.LOADING
        if (timeNow.isAfter(timeMorning) && timeNow.isBefore(timeNoon)){
            helloStatus.value = -1
            _status.value = LoadApiStatus.DONE
        } else if (timeNow.isAfter(timeNoon) && timeNow.isBefore(timeAfternoon)) {
            helloStatus.value = -2
            _status.value = LoadApiStatus.DONE
        } else if (timeNow.isAfter(timeAfternoon) && timeNow.isBefore(timeNight)) {
            helloStatus.value = -3
            _status.value = LoadApiStatus.DONE
        } else if (timeNow.isAfter(timeNight) && timeNow.isBefore(timeMidnight)) {
            helloStatus.value = -4
            _status.value = LoadApiStatus.DONE
        } else {
            helloStatus.value = -5
            _status.value = LoadApiStatus.DONE
        }
    }

    private fun getUsersResult(startTime: Long, endTime: Long) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getUsersDiarys(startTime, endTime)

            _diary.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    Log.i("getUsersResult","getUsersResult = ${result.data}")
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

    fun getStartTime(): Long =
        TimeConverters.dateToTimestamp(LocalDate.now().toString(), Locale.TAIWAN).minus(518348572)

    //get LocaleDate's 00:00 and plus into 23:59
    fun getEndTime(): Long =
        TimeConverters.dateToTimestamp(LocalDate.now().toString(), Locale.TAIWAN).plus(86391428)

    fun getSameStore(diarys: List<Diary>){

        var midStore = ""

        diarys.forEach { item ->
            item.store?.storeName.let {
                if (it != null) {
                    sameStore.add(it)
                }
            }
        }

        for ( item in sameStore ){
            val test = sameStore.filter { it == item }
            if (test.count() > count.value!!){
                count.value = test.count()
                midStore = test.first()
                sameStoreStatus.value == true
            } else {
                sameStoreStatus.value == false
            }
            listStore.value = midStore
//            Log.d("getSameStore","getSameStore = ${test.count()}; ${test}")
        }
//        Log.d("getSameStore","listStore = $listStore")
    }

    fun getHealthy(){
        diary.value?.forEach {
            it.food?.healthyScore.let {
                if (it != null) {
                    score = score + (it.toInt())
                }
            }
        }
        listSize = diary.value?.size?.toFloat() ?: 1F
        if ( listSize == 0F) {
            null
        } else {
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

}
