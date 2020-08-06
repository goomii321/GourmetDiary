package com.linda.gourmetdiary.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.linda.gourmetdiary.data.Store
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.stores.detail.StoreDetailViewModel

@Suppress("UNCHECKED_CAST")
class StoresViewModelFactory constructor(
    private val diaryRepository: DiaryRepository,
    private val store: Store?
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(StoreDetailViewModel::class.java) ->
                    StoreDetailViewModel(diaryRepository,store)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
