package com.linda.gourmetdiary.diarys.detail

import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.network.LoadApiStatus

class DiaryDetailViewModel(private val diaryRepository: DiaryRepository,
                           private val arguments: Diary?) : ViewModel() {
    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    private val _detail = MutableLiveData<Diary>()
    val detail: LiveData<Diary>
        get() = _detail

    private val _diary = MutableLiveData<Diary>().apply {
        value = arguments
    }
    val diary: LiveData<Diary>
        get() = _diary

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
