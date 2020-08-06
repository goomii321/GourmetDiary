package com.linda.gourmetdiary.diaries

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linda.gourmetdiary.network.LoadApiStatus
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.data.*
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.util.TimeConverters
import com.linda.gourmetdiary.util.timastampOfDay
import com.linda.gourmetdiary.util.timastampOfWeek
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*

class DiariesViewModel(private val repository: DiaryRepository) : ViewModel() {

    var calendarYear = 0
    var calendarMonth = 0
    var calendarDay = 0

    var saveYear = MutableLiveData<Int>()
    var saveMonth = MutableLiveData<Int>()
    var saveDay = MutableLiveData<Int>()

    var endTime = MutableLiveData<Long>()
    var startTime = MutableLiveData<Long>().value

    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    var liveDiary = MutableLiveData<List<Diary>>()

    private var _diary = MutableLiveData<List<Diary>>()
    val diary: LiveData<List<Diary>>
        get() = _diary

    var diaries4Days = mutableListOf<Diaries4Day>()

    private var _dataItems = MutableLiveData<List<DataItem>>()
    val dataItems: LiveData<List<DataItem>>
        get() = _dataItems

    private val _refreshStatus = MutableLiveData<Boolean>()
    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    private val _navigateToDetail = MutableLiveData<Diary>()
    val navigateToDetail: LiveData<Diary>
        get() = _navigateToDetail

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getUsersResult(getTodayStartTime(), getTodayEndTime())
    }

    // current time ~ six days ago = 604740000
    //plus to 23:59 and minus to six days ago
    fun getTodayStartTime(): Long =
        TimeConverters.dateToTimestamp(LocalDate.now().toString(), Locale.TAIWAN).minus(timastampOfWeek)

    //get LocaleDate's 00:00 and plus into 23:59
    fun getTodayEndTime(): Long =
        TimeConverters.dateToTimestamp(LocalDate.now().toString(), Locale.TAIWAN).plus(timastampOfDay)

    fun getUsersResult(startTime: Long, endTime: Long) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getDiaries(startTime, endTime)

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

    fun assignData(diaries: List<Diary>) {

        diaries.forEach { diary ->
            diary.eatingTime?.let {
                var converte = TimeConverters.timestampToDate(it, Locale.TAIWAN)
                var condition = TimeConverters.dateToTimestamp(converte, Locale.TAIWAN)
                val diaries4Day = Diaries4Day()

                if (diaries4Days.none { it.dayTitle == condition }) { // if diarys4Days doesn't include day title of this day
                    diaries4Day.dayTitle = condition
                    diaries4Day.diaries.add(diary)
                    diaries4Days.add(diaries4Day)

                } else { // if diarys4Days include day title of this day
                    diaries4Days.find { it.dayTitle == condition }?.diaries?.add(diary)

                }
            }
        }

        _dataItems.value = diariesToDataItems(diaries4Days = diaries4Days)
    }

    fun diariesToDataItems(title: String = "選擇其他日期", diaries4Days: MutableList<Diaries4Day>): List<DataItem> {
        val dataList = mutableListOf<DataItem>()

        dataList.add(DataItem.Title(title))
        diaries4Days.forEach { diaryList ->
            diaryList.diaries.sortBy { it.eatingTime }
            dataList.add(DataItem.Diaries(diaryList))
        }

        return dataList
    }

    fun onDataAssigned() {
        _diary.value = null
        diaries4Days.clear()
    }
}
