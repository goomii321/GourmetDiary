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
import java.time.LocalDate
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

    private var sameStore = mutableListOf<String>()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val isLoggedIn
        get() = UserManager.isLoggedIn

    var count = MutableLiveData<Int>().apply { value = 0 }
    var listStore =MutableLiveData<String>()
    var countText = MutableLiveData<String>()
    var listStoreText = MutableLiveData<String>()

    init {
        getUsersResult(getStartTime(),getEndTime())
    }

    private fun getUsersResult(startTime: Long, endTime: Long) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getUsersDiarys(startTime, endTime)

            _diary.value = when (result) {
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
            _refreshStatus.value = false
            getSameStore(diary.value!!)
        }
    }

    fun getStartTime(): Long =
        TimeConverters.dateToTimestamp(LocalDate.now().toString(), Locale.TAIWAN).minus(518348572)

    //get LocaleDate's 00:00 and plus into 23:59
    fun getEndTime(): Long =
        TimeConverters.dateToTimestamp(LocalDate.now().toString(), Locale.TAIWAN).plus(86391428)

    fun getSameStore(diarys: List<Diary>){

        diarys.forEach { item ->
            item.store?.storeName.let {
                if (it != null) {
                    sameStore.add(it)
                }
            }
        }
//        Log.d("getSameStore","sameStore = $sameStore")

        for ( item in sameStore ){
            val test = sameStore.filter { it == item }
            if (test.count() > count.value!!){
                count.value = test.count()
            }
            listStore.value = test.first()
        }
//        Log.d("getSameStore","listStore = ${listStore.value}; count = ${count.value}")
    }

    fun clearReminder(){
        count.value = null
        listStore.value = null
        countText.value = null
        listStoreText.value = null
    }
}
