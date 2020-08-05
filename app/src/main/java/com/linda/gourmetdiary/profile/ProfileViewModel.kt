package com.linda.gourmetdiary.profile

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
import com.linda.gourmetdiary.util.timastampOfDay
import com.linda.gourmetdiary.util.timastampOfWeek
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.*

class ProfileViewModel(private val repository: DiaryRepository) : ViewModel() {

    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private var _diaryList = MutableLiveData<List<Diary>>()
    val diaryList: LiveData<List<Diary>>
        get() = _diaryList

    val diaryCount = MutableLiveData<Int>()
    val storeCount = MutableLiveData<Int>()
    val weeklyCost = MutableLiveData<Int>()
    val healthyScore = MutableLiveData<String>()

    var diarys4Days = mutableListOf<Diarys4Day>()
    val diary4Day = MutableLiveData<List<Diarys4Day>>()
    var diarys4DaysStatus = MutableLiveData<Boolean>().apply { value = false }

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getDiaryResult(getTodayStartTime(), getTodayEndTime())
        queryDiaryCount()
        queryStoreCount()
    }

    private fun getTodayStartTime(): Long =
        TimeConverters.dateToTimestamp(LocalDate.now().toString(), Locale.TAIWAN).minus(timastampOfWeek)

    //get LocaleDate's 00:00 and plus into 23:59
    private fun getTodayEndTime(): Long =
        TimeConverters.dateToTimestamp(LocalDate.now().toString(), Locale.TAIWAN).plus(timastampOfDay)

    private fun queryDiaryCount() {
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

    private fun queryStoreCount() {
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

    private fun getDiaryResult(startTime: Long, endTime: Long) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getDiarys(startTime, endTime)

            _diaryList.value = when (result) {
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

    private fun getCost(){
        var cost = 0
        _status.value = LoadApiStatus.LOADING

        diaryList.value?.forEach {
            it.food?.price.let {price ->
                if (price != null) {
                    cost += (price.toInt())
                }
            }
            weeklyCost.value = cost
        }
        _status.value = LoadApiStatus.DONE
    }

    private fun getHealthy(){
        var score = 0F
        var listSize = 1F
        var scoreAverage = 0F
        _status.value = LoadApiStatus.LOADING

        diaryList.value?.forEach {diary ->
            diary.food?.healthyScore.let {
                if (it != null) {
                    score += (it.toInt())
                }
            }
        }
        listSize = diaryList.value?.size?.toFloat() ?: 1F

        if ( listSize != 0F) {
            scoreAverage = score/listSize
            healthyScore.value = BigDecimal(scoreAverage.toString()).setScale(1, RoundingMode.HALF_DOWN).toString()
        }
        _status.value = LoadApiStatus.DONE
    }

    fun assignDiaryData(diaryList: List<Diary>) {
        diarys4DaysStatus.value = false

        diaryList.forEach { diary ->
            diary.eatingTime?.let { eatingTime ->
                val converter = TimeConverters.timestampToDate(eatingTime, Locale.TAIWAN)
                val condition = TimeConverters.dateToTimestamp(converter, Locale.TAIWAN)
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
        diary4Day.value = diarys4Days
        diarys4DaysStatus.value = true
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
