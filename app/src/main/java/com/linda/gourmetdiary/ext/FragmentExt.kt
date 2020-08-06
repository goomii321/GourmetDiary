package com.linda.gourmetdiary.ext

import androidx.fragment.app.Fragment
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.data.Store
import com.linda.gourmetdiary.factory.DiarysViewModelFactory
import com.linda.gourmetdiary.factory.StoresViewModelFactory
import com.linda.gourmetdiary.factory.ViewModelFactory

/**
 * Created by Wayne Chen in Jul. 2019.
 *
 * Extension functions for Fragment.
 */
fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as DiaryApplication).diaryRepository
    return ViewModelFactory(repository)
}

fun Fragment.getVmFactory(diarys: Diary?): DiarysViewModelFactory {
    val repository = (requireContext().applicationContext as DiaryApplication).diaryRepository
    return DiarysViewModelFactory(repository, diarys)
}

fun Fragment.getVmFactory(store: Store?): StoresViewModelFactory {
    val repository = (requireContext().applicationContext as DiaryApplication).diaryRepository
    return StoresViewModelFactory(repository, store)
}
