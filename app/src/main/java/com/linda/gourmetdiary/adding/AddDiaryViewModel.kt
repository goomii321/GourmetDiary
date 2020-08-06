package com.linda.gourmetdiary.adding

import android.util.Log
import android.widget.Toast
import androidx.databinding.InverseMethod
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

    private val _user = MutableLiveData<Diary>().apply {
        value = Diary( food = Food(), store = Store())
    }
    val user: LiveData<Diary>
        get() = _user

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
        foodStatus.value = !foodStatus.value!!
    }

    fun setStoreVisible() {
        storeStatus.value = !storeStatus.value!!
    }

    fun addData(diarys: Diary) {

        when {
            user.value?.food?.foodName.isNullOrEmpty() -> _invalidCheckout.value = -1
            user.value?.eatingTime?.toString() == "0" -> _invalidCheckout.value = -2
            user.value?.store?.storeName.isNullOrEmpty() -> _invalidCheckout.value = -3
            user.value?.mainImage == "" -> _invalidCheckout.value = -4
            updateImageStatus.value == false -> _invalidCheckout.value = -5
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