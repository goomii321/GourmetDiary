package com.linda.gourmetdiary.stores.detail

import android.util.Log
import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.data.Stores
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.linda.gourmetdiary.data.Result
import java.math.BigDecimal
import java.math.RoundingMode

class StoreDetailViewModel(private val diaryRepository: DiaryRepository,
                           private val arguments: Stores?) : ViewModel() {
    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _detail = MutableLiveData<Stores>()
    val detail: LiveData<Stores>
        get() = _detail

    private val _store = MutableLiveData<Stores>().apply {
        value = arguments }
    val store: LiveData<Stores>
        get() = _store

    private val _history = MutableLiveData<List<Diary>>()
    val history: LiveData<List<Diary>>
        get() = _history

    private val _navigateToDiary = MutableLiveData<Diary>()
    val navigateToDiary: LiveData<Diary>
        get() = _navigateToDiary

    var vistitTimes = MutableLiveData<String>()
    var allCost = MutableLiveData<String>()
    var healthyText = MutableLiveData<String>()
    var rateText = MutableLiveData<String>()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)
    
    init {
//        Log.d("checkBranch","store branch is ${_store.value?.storeBranch}")
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

    private fun visitTimes(){
        vistitTimes.value = history.value?.size.toString()
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

    fun calculateHealthy() {
        var score = 0F
        var listSize = 1F
        var scoreAverage = 1F
        history.value?.forEach { number ->
            number.food?.healthyScore.let {
                score = score.plus(it?.toFloat()!!)
            }
        }
        listSize = history.value?.size?.toFloat() ?: 1F
        scoreAverage = score/listSize

        healthyText.value = BigDecimal(scoreAverage.toString()).setScale(1,RoundingMode.HALF_DOWN).toString()
    }

    fun calculateRate()  {
        var score = 0F
        var listSize = 1F
        var scoreAverage = 1F
        history.value?.forEach { number ->
            number.food?.foodRate.let {
                score = score.plus(it?.toFloat()!!)
            }
        }
        listSize = history.value?.size?.toFloat() ?: 1F
        scoreAverage = score/listSize
        rateText.value = BigDecimal(scoreAverage.toString()).setScale(1,RoundingMode.HALF_DOWN).toString()
    }

}
