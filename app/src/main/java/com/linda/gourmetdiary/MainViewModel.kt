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

//    val currentDrawerToggleType: LiveData<DrawerToggleType> = Transformations.map(currentFragmentType) {
//        when (it) {
//
//            else -> DrawerToggleType.NORMAL
//        }
//    }

}