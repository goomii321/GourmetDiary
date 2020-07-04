package com.linda.gourmetdiary.data.source

import com.linda.gourmetdiary.data.Result
import com.linda.gourmetdiary.data.Users

/**
 * Created by Wayne Chen in Jul. 2019.
 *
 * Concrete implementation to load Stylish sources.
 */
class DefaultDiaryRepository(private val diaryRemoteDataSource: DiaryDataSource,
                             private val localDataSource: DiaryDataSource
) : DiaryRepository {
//    override suspend fun getUsers(): Result<List<Users>> {
//        return diaryRemoteDataSource.getUsers()
//    }
}
