package com.linda.gourmetdiary.ext

import androidx.fragment.app.Fragment
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.data.Store
import com.linda.gourmetdiary.factory.DiariesViewModelFactory
import com.linda.gourmetdiary.factory.StoresViewModelFactory
import com.linda.gourmetdiary.factory.ViewModelFactory


fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as DiaryApplication).diaryRepository
    return ViewModelFactory(repository)
}

fun Fragment.getVmFactory(diaries: Diary?): DiariesViewModelFactory {
    val repository = (requireContext().applicationContext as DiaryApplication).diaryRepository
    return DiariesViewModelFactory(repository, diaries)
}

fun Fragment.getVmFactory(store: Store?): StoresViewModelFactory {
    val repository = (requireContext().applicationContext as DiaryApplication).diaryRepository
    return StoresViewModelFactory(repository, store)
}
