package com.linda.gourmetdiary.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.linda.gourmetdiary.adding.AddDiaryViewModel
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.data.Users
import com.linda.gourmetdiary.data.source.DiaryRepository

/**
 * Created by Wayne Chen in Jul. 2019.
 *
 * Factory for all ViewModels.
 */
@Suppress("UNCHECKED_CAST")
class DiarysViewModelFactory constructor(
    private val diaryRepository: DiaryRepository,
    private val diarys: Diary?
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(AddDiaryViewModel::class.java) ->
                    AddDiaryViewModel(diaryRepository,diarys)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
