package com.linda.gourmetdiary.profile

import android.util.Log
import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.data.Diarys4Day
import com.linda.gourmetdiary.data.Result
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.network.LoadApiStatus
import com.linda.gourmetdiary.util.TimeConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.*
import java.util.logging.Logger
import kotlin.math.cos

class ProfileViewModel(private val repository: DiaryRepository) : ViewModel() {

    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private var _diary = MutableLiveData<List<Diary>>()
    val diary: LiveData<List<Diary>>
        get() = _diary

    val diaryCount = MutableLiveData<Int>()
    val storeCount = MutableLiveData<Int>()
    val weekulyCost = MutableLiveData<Int>()
    val healthyScore = MutableLiveData<String>()

    var diarys4Days = mutableListOf<Diarys4Day>()
    var diarys4DaysStatus = MutableLiveData<Boolean>().apply { value = false }

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getUsersResult(getTodayStartTime(), getTodayEndTime())
        queryDiaryCount()
        queryStoreCount()
    }

    fun getTodayStartTime(): Long =
        TimeConverters.dateToTimestamp(LocalDate.now().toString(), Locale.TAIWAN).minus(518348572)

    //get LocaleDate's 00:00 and plus into 23:59
    fun getTodayEndTime(): Long =
        TimeConverters.dateToTimestamp(LocalDate.now().toString(), Locale.TAIWAN).plus(86391428)

    fun queryDiaryCount() {
        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.queryDiaryCount()

            diaryCount.value = when (result) {
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

    fun queryStoreCount() {
        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.queryStoreCount()

            storeCount.value = when (result) {
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

    private fun getUsersResult(startTime: Long, endTime: Long) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getUsersDiarys(startTime, endTime)

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
            getCost()
            getHealthy()
        }
    }

    fun getCost(){
        var cost = 0
        _status.value = LoadApiStatus.LOADING
        diary.value?.forEach {
            it.food?.price.let {
                if (it != null) {
                    cost = cost + (it.toInt())
                }
            }
//            Log.d("getCost","getCost = $cost ; ${it.food?.price}")
            weekulyCost.value = cost
        }
        _status.value = LoadApiStatus.DONE
    }

    fun getHealthy(){
        var score = 0F
        var listSize = 1F
        var scoreAverage = 0F
        _status.value = LoadApiStatus.LOADING
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
        Log.d("scoreAverage","scoreAverage = $scoreAverage ; listSize = $listSize ; score = $score")
        _status.value = LoadApiStatus.DONE
    }

    fun assignDiaryData(diarys: List<Diary>) {
        diarys4DaysStatus.value = false
        diarys.forEach { diary ->
            diary.eatingTime?.let {
                var converte = TimeConverters.timestampToDate(it, Locale.TAIWAN)
                var condition = TimeConverters.dateToTimestamp(converte, Locale.TAIWAN)
                val diarys4Day = Diarys4Day()

                if (diarys4Days.none { it.dayTitle == condition }) { // if diarys4Days doesn't include day title of this day
                    diarys4Day.dayTitle = condition
                    diarys4Day.diarys.add(diary)
                    diarys4Days.add(diarys4Day)
                } else { // if diarys4Days include day title of this day
                    diarys4Days.find { it.dayTitle == condition }?.diarys?.add(diary)
                }
            }
//            diarys4Days.forEach {
//                Log.i("diarys4Days", "dayTitle = ${it.dayTitle} ")
//                it.diarys.forEach {
//                    Log.d("diarys4Days", "diary = ${it} ")
//                }
//            }
        }
        diarys4DaysStatus.value = true
//        getDailyCost()
    }

    fun getDailyCost(){
        var money = 0
        diarys4Days.forEach{
            it.diarys.forEach {
                money = money + (it.food?.price?.toInt() ?: 0)
            }
        }
//        Log.d("money"," $$$ is $money")
    }

    fun onDataAssigned() {
        _diary.value = null
        diarys4Days.clear()
    }

    @InverseMethod("convertLongToString")
    fun convertStringToLong(value: String): Int {
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

    fun convertLongToString(value: Int): String {
        return value.toString()
    }
}
