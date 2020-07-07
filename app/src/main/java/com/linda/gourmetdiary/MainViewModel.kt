package com.linda.gourmetdiary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.util.CurrentFragmentType
import com.linda.gourmetdiary.util.DrawerToggleType

class MainViewModel(): ViewModel() {

    val currentFragmentType = MutableLiveData<CurrentFragmentType>()

    private val _refresh = MutableLiveData<Boolean>()
    val refresh: LiveData<Boolean>
        get() = _refresh

    fun refresh() {
        if (!DiaryApplication.instance.isLiveDataDesign()) {
            _refresh.value = true
        }
    }

    fun onRefreshed() {
        if (!DiaryApplication.instance.isLiveDataDesign()) {
            _refresh.value = null
        }
    }

}