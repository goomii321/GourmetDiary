package com.linda.gourmetdiary.data.source

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Created by Wayne Chen in Jul. 2019.
 *
 * Concrete implementation to load Stylish sources.
 */
class DefaultDiaryRepository(private val diaryRemoteDataSource: DiaryDataSource,
                             private val diaryLocalDataSource: DiaryDataSource,
                             private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : DiaryRepository {


}
