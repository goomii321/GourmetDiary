package com.linda.gourmetdiary.stores.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linda.gourmetdiary.data.Stores
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.network.LoadApiStatus

class StoreDetailViewModel(private val diaryRepository: DiaryRepository,
                           private val arguments: Stores?) : ViewModel() {
    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    private val _detail = MutableLiveData<Stores>()
    val detail: LiveData<Stores>
        get() = _detail

    private val _store = MutableLiveData<Stores>().apply {
        value = arguments
    }
    val store: LiveData<Stores>
        get() = _store
}
