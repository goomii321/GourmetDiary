package com.linda.gourmetdiary.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.linda.gourmetdiary.MainViewModel
import com.linda.gourmetdiary.adding.AddDiaryViewModel
import com.linda.gourmetdiary.data.Diarys
import com.linda.gourmetdiary.data.Users
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
class DiarysViewModelFactory constructor(
    private val diaryRepository: DiaryRepository,
    private val users: Users?
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(AddDiaryViewModel::class.java) ->
                    AddDiaryViewModel(diaryRepository,users)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
