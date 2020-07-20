package com.linda.gourmetdiary.data.source

import androidx.lifecycle.MutableLiveData
import com.linda.gourmetdiary.data.*

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

    override suspend fun queryStoreCount(): Result<Int> {
        return diaryRemoteDataSource.queryStoreCount()
    }

    override suspend fun pushProfile(user: User): Result<Boolean> {
        return diaryRemoteDataSource.pushProfile(user)
    }

    override suspend fun queryStoreHistory(storeName:String): Result<List<Diary>> {
        return diaryRemoteDataSource.queryStoreHistory(storeName)
    }

    override suspend fun queryReminder(): Result<List<Diary>> {
        return diaryRemoteDataSource.queryReminder()
    }

    override suspend fun searchTemplate(searchWord: String): Result<List<Diary>> {
        return diaryRemoteDataSource.searchTemplate(searchWord)
    }
}
