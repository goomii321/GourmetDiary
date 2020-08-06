package com.linda.gourmetdiary.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.diaries.detail.DiaryDetailViewModel

@Suppress("UNCHECKED_CAST")
class DiariesViewModelFactory constructor(
    private val diaryRepository: DiaryRepository,
    private val diaries: Diary?
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
//                isAssignableFrom(AddDiaryViewModel::class.java) ->
//                    AddDiaryViewModel(diaryRepository,diarys)
                isAssignableFrom(DiaryDetailViewModel::class.java) ->
                    DiaryDetailViewModel(diaryRepository,diaries)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
