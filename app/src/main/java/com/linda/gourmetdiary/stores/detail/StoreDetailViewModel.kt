package com.linda.gourmetdiary.stores.detail

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.data.Store
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.linda.gourmetdiary.data.Result
import com.linda.gourmetdiary.util.Logger
import java.math.BigDecimal
import java.math.RoundingMode

class StoreDetailViewModel(private val diaryRepository: DiaryRepository,
                           private val arguments: Store?) : ViewModel() {
    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _store = MutableLiveData<Store>().apply {
        value = arguments }
    val store: LiveData<Store>
        get() = _store

    val storeImage = MutableLiveData<String>()

    private val _history = MutableLiveData<List<Diary>>()
    val history: LiveData<List<Diary>>
        get() = _history

    private val _navigateToDiary = MutableLiveData<Diary>()
    val navigateToDiary: LiveData<Diary>
        get() = _navigateToDiary

    var visitTimes = MutableLiveData<String>()
    var allCost = MutableLiveData<String>()
    var healthyText = MutableLiveData<String>()
    var rateText = MutableLiveData<String>()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)
    
    init {
        queryHistory("${_store.value?.storeName}","${_store.value?.storeBranch}")
    }

    private fun queryHistory(storeName:String,storeBranch:String) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = diaryRepository.queryStoreHistory(storeName,storeBranch)

            _history.value = when (result) {
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
            visitTimes()
            getCost()
            calculateHealthy()
            calculateRate()
        }
    }

    fun uploadImage(uri: Uri) {

        coroutineScope.launch {

            val result = diaryRepository.uploadImage(uri)

            _store.value?.storeImage = when (result) {
                is Result.Success -> {
                    _error.value = null
                    Logger.i("diary images = ${_store.value?.storeImage}")
                    storeImage.value = result.data
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    null
                }
                else -> {
                    _error.value = DiaryApplication.instance.getString(R.string.ng_msg)
                    null
                }
            }
        }
    }

    fun updateImage(store: Store) {
        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = diaryRepository.updateStoreImage(store)) {
                is Result.Success -> {
                    _error.value = null
                    Logger.d("updateImage Success")
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

    private fun visitTimes(){
        visitTimes.value = history.value?.size.toString()
    }

    private fun getCost(){
        var cost = 0
        history.value?.forEach { history ->
            history.food?.price?.let {
                cost = cost.plus(it.toInt())
            }
        }
        allCost.value = cost.toString()
    }

    fun navigateToDiary(diary: Diary) {
        _navigateToDiary.value = diary
    }

    fun onDiaryNavigated() {
        _navigateToDiary.value = null
    }

    private fun calculateHealthy() {
        var score = 0F
        var listSize = 1F
        var scoreAverage = 1F
        history.value?.forEach {
            it.food?.healthyScore.let {number ->
                if (number != null) {
                    score = score.plus(number.toFloat())
                }
            }
        }
        listSize = history.value?.size?.toFloat() ?: 1F
        scoreAverage = score/listSize

        healthyText.value = BigDecimal(scoreAverage.toString()).setScale(1,RoundingMode.HALF_DOWN).toString()
    }

    private fun calculateRate()  {
        var score = 0F
        var listSize = 1F
        var scoreAverage = 1F
        history.value?.forEach { number ->
            number.food?.foodRate.let { rateValue ->
                rateValue?.let {
                    score = score.plus(rateValue.toFloat())
                }
            }
        }
        listSize = history.value?.size?.toFloat() ?: 1F
        scoreAverage = score/listSize
        rateText.value = BigDecimal(scoreAverage.toString()).setScale(1,RoundingMode.HALF_DOWN).toString()
    }

}
