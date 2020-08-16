package com.linda.gourmetdiary.data.source

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.linda.gourmetdiary.data.*

/**
 * Created by Wayne Chen in Jul. 2019.
 *
 * Main entry point for accessing Stylish sources.
 */
interface DiaryDataSource {

    suspend fun getDiaries(startTime:Long, endTime: Long): Result<List<Diary>>

    suspend fun postDiary(diaries:Diary): Result<Boolean>

    suspend fun updateStoreImage(store: Store): Result<Boolean>

    fun getLiveDiary(startTime:Long , endTime: Long): MutableLiveData<List<Diary>>

    suspend fun getStore(): Result<List<Store>>

    fun getLiveStore(): MutableLiveData<List<Store>>

    suspend fun queryDiaryCount(): Result<Int>

    suspend fun queryStoreCount(): Result<Int>

    suspend fun pushProfile(user: User): Result<Boolean>

    suspend fun queryStoreHistory(storeName:String, storeBranch:String): Result<List<Diary>>

    suspend fun queryReminder(): Result<List<Diary>>

    suspend fun searchTemplate(searchWord: String): Result<List<Diary>>

    suspend fun uploadImage(uri: Uri): Result<String>
}
