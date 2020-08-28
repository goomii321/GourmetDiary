package com.linda.gourmetdiary.data.source.local

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.linda.gourmetdiary.data.*
import com.linda.gourmetdiary.data.source.DiaryDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class DiaryLocalDataSource(val context: Context) : DiaryDataSource {
    override suspend fun getDiaries(startTime:Long, endTime: Long): Result<List<Diary>> {
        TODO("Not yet implemented")
    }

    override suspend fun postDiary(diaries: Diary): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun updateStoreImage(store: Store): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getLiveDiary(startTime:Long , endTime: Long): MutableLiveData<List<Diary>> {
        TODO("Not yet implemented")
    }

    override suspend fun getStore(): Result<List<Store>> {
        TODO("Not yet implemented")
    }

    override fun getLiveStore(): MutableLiveData<List<Store>> {
        TODO("Not yet implemented")
    }

    override suspend fun queryDiaryCount(): Result<Int> {
        TODO("Not yet implemented")
    }

    override suspend fun queryStoreCount(): Result<Int> {
        TODO("Not yet implemented")
    }

    override suspend fun pushProfile(user: User): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun queryStoreHistory(storeName:String, storeBranch:String): Result<List<Diary>> {
        TODO("Not yet implemented")
    }

    override suspend fun queryReminder(): Result<List<Diary>> {
        TODO("Not yet implemented")
    }

    override suspend fun searchTemplate(searchWord: String): Result<List<Diary>> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadImage(uri: Uri): Result<String> {
        TODO("Not yet implemented")
    }
}
