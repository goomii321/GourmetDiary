package com.linda.gourmetdiary.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.linda.gourmetdiary.MainViewModel
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.diarys.DiarysViewModel
import com.linda.gourmetdiary.home.HomeViewModel
import com.linda.gourmetdiary.stores.StoresViewModel

/**
 * Created by Wayne Chen in Jul. 2019.
 *
 * Factory for all ViewModels.
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val diaryRepository: DiaryRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel()

                isAssignableFrom(HomeViewModel::class.java) ->
                    HomeViewModel()

                isAssignableFrom(DiarysViewModel::class.java) ->
                    DiarysViewModel(diaryRepository)

                isAssignableFrom(StoresViewModel::class.java) ->
                    StoresViewModel(diaryRepository)


                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
