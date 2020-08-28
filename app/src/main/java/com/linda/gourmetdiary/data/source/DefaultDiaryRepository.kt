package com.linda.gourmetdiary.data.source

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.linda.gourmetdiary.data.*

class DefaultDiaryRepository(private val diaryRemoteDataSource: DiaryDataSource,
                             private val localDataSource: DiaryDataSource
) : DiaryRepository {
    override suspend fun getDiaries(startTime:Long, endTime: Long): Result<List<Diary>> {
        return diaryRemoteDataSource.getDiaries(startTime, endTime)
    }

    override suspend fun postDiary(diaries: Diary): Result<Boolean> {
        return diaryRemoteDataSource.postDiary(diaries)
    }

    override suspend fun updateStoreImage(store: Store): Result<Boolean> {
        return diaryRemoteDataSource.updateStoreImage(store)
    }

    override fun getLiveDiary(startTime:Long , endTime: Long): MutableLiveData<List<Diary>> {
        return diaryRemoteDataSource.getLiveDiary(startTime,endTime)
    }

    override suspend fun getStore(): Result<List<Store>> {
        return diaryRemoteDataSource.getStore()
    }

    override fun getLiveStore(): MutableLiveData<List<Store>> {
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

    override suspend fun queryStoreHistory(storeName:String, storeBranch:String): Result<List<Diary>> {
        return diaryRemoteDataSource.queryStoreHistory(storeName,storeBranch)
    }

    override suspend fun queryReminder(): Result<List<Diary>> {
        return diaryRemoteDataSource.queryReminder()
    }

    override suspend fun searchTemplate(searchWord: String): Result<List<Diary>> {
        return diaryRemoteDataSource.searchTemplate(searchWord)
    }

    override suspend fun uploadImage(uri: Uri): Result<String> {
        return diaryRemoteDataSource.uploadImage(uri)
    }
}
