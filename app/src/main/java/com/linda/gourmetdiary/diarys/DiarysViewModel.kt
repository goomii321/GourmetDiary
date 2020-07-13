package com.linda.gourmetdiary.diarys

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linda.gourmetdiary.network.LoadApiStatus
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.data.*
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.util.TimeConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*

class DiarysViewModel(private val repository: DiaryRepository) : ViewModel() {

    var endTime = MutableLiveData<Long>()
    var startTime = MutableLiveData<Long>().value

    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    //Diary
    var liveDiary = MutableLiveData<List<Diary>>()

    private var _diary = MutableLiveData<List<Diary>>()
    val diary: LiveData<List<Diary>>
        get() = _diary

    // diarys4Day
    var liveDiarys4Day = MutableLiveData<List<Diarys4Day>>()

    var diarys4Days = mutableListOf<Diarys4Day>()

    private val _refreshStatus = MutableLiveData<Boolean>()
    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    private val _navigateToDetail = MutableLiveData<Diary>()
    val navigateToDetail: LiveData<Diary>
        get() = _navigateToDetail

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
//        if (DiaryApplication.instance.isLiveDataDesign()) {
//            getLiveDiaryResult(getTodayStartTime(), getTodayEndTime())
//        } else {
            getUsersResult(getTodayStartTime(), getTodayEndTime())
        Log.i("checkTime","start time = ${TimeConverters.timeStampToTime(getTodayStartTime(),
            Locale.TAIWAN)}; end time = ${TimeConverters.timeStampToTime(getTodayEndTime(),Locale.TAIWAN)}")
//        assignData(diary.value!!)
//        }
    }

    // current time ~ six days ago = 604740000
    //plus to 23:59 and minus to six days ago
    fun getTodayStartTime(): Long =
        TimeConverters.dateToTimestamp(LocalDate.now().toString(), Locale.TAIWAN).minus(518348572)

    //get LocaleDate's 00:00 and plus into 23:59
    fun getTodayEndTime(): Long =
        TimeConverters.dateToTimestamp(LocalDate.now().toString(), Locale.TAIWAN).plus(86391428)

    fun getLiveDiaryResult(startTime: Long, endTime: Long) {
        liveDiary = repository.getLiveDiary(startTime, endTime)
        Log.i("diary", "getLiveDiaryResult diary = ${liveDiary.value}")
        _status.value = LoadApiStatus.DONE
        _refreshStatus.value = false
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
            _refreshStatus.value = false
        }
    }

    fun refresh() {

        if (DiaryApplication.instance.isLiveDataDesign()) {
            _status.value = LoadApiStatus.DONE
            _refreshStatus.value = false

        } else {
            if (status.value != LoadApiStatus.LOADING) {
                startTime?.let { endTime.value?.let { it1 -> getUsersResult(it, it1) } }
            }
        }
    }

    fun navigateToDetail(diary: Diary) {
        _navigateToDetail.value = diary
    }

    fun onDetailNavigated() {
        _navigateToDetail.value = null
    }

    fun assignData(diary: List<Diary>) {

        _diary.value?.forEach { diary ->
            diary.eatingTime?.let {
                var converte = TimeConverters.timestampToDate(it, Locale.TAIWAN)
                var condition = TimeConverters.dateToTimestamp(converte, Locale.TAIWAN)
                val diarys4Day = Diarys4Day()

                if (diarys4Days.none { it.dayTitle == condition }) { // if diarys4Days doesn't include day title of this day
                    diarys4Day.dayTitle = condition
                    diarys4Day.diarys.add(diary)
                    diarys4Days.add(diarys4Day)
                    Log.i("converter", "condition date = $converte ;  $condition ; ${diarys4Day.dayTitle} ")
                } else { // if diarys4Days include day title of this day
                    diarys4Days.find { it.dayTitle == condition }?.diarys?.add(diary)
                    Log.i("converter2", "condition date = $converte ;  $condition ; ${diarys4Day.dayTitle} ")
                }
            }

            diarys4Days.forEach {
                Log.i("diarys4Days","dayTitle = ${it.dayTitle} ")
                it.diarys.forEach {
                    Log.d("diarys4Days","diary = ${it} ")
                }
            }
        }
    }
}
