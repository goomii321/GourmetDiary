package com.linda.gourmetdiary.adding

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linda.gourmetdiary.network.LoadApiStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.data.*
import com.linda.gourmetdiary.data.source.DiaryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class AddDiaryViewModel(private val repository: DiaryRepository,
                        private val diarys: Diary?) : ViewModel() {

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

    private val _user = MutableLiveData<Users>().apply {
        value = Users(
            diarys = Diary(food = Food(), store = Store())
        )
    }
    val user: LiveData<Users>
        get() = _user

    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

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
    val imageValue = MutableLiveData<String>()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun setFoodVisible() {
        foodStatus.value = !foodStatus.value!!
    }

    fun setStoreVisible() {
        storeStatus.value = !storeStatus.value!!
    }

    fun addData(diarys: Diary) {
        coroutineScope.launch {

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