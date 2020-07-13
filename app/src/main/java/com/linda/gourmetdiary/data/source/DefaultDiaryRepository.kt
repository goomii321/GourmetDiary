package com.linda.gourmetdiary.data.source

import androidx.lifecycle.MutableLiveData
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.data.Result
import com.linda.gourmetdiary.data.Stores
import com.linda.gourmetdiary.data.Users

/**
 * Created by Wayne Chen in Jul. 2019.
 *
 * Concrete implementation to load Stylish sources.
 */
class DefaultDiaryRepository(private val diaryRemoteDataSource: DiaryDataSource,
                             private val localDataSource: DiaryDataSource
) : DiaryRepository {
    override suspend fun getUsersDiarys(startTime:Long , endTime: Long): Result<List<Diary>> {
        return diaryRemoteDataSource.getUsersDiarys(startTime, endTime)
    }

    override suspend fun postDiary(diarys: Diary): Result<Boolean> {
        return diaryRemoteDataSource.postDiary(diarys)
    }

    override fun getLiveDiary(startTime:Long , endTime: Long): MutableLiveData<List<Diary>> {
        return diaryRemoteDataSource.getLiveDiary(startTime,endTime)
    }

    override suspend fun getStore(): Result<List<Stores>> {
        return diaryRemoteDataSource.getStore()
    }

    override fun getLiveStore(): MutableLiveData<List<Stores>> {
        return diaryRemoteDataSource.getLiveStore()
    }

    override suspend fun queryDiaryCount(): Result<Int> {
        return diaryRemoteDataSource.queryDiaryCount()
    }
}
