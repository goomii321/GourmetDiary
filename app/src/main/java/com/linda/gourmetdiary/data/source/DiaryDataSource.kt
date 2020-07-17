package com.linda.gourmetdiary.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.linda.gourmetdiary.data.*

/**
 * Created by Wayne Chen in Jul. 2019.
 *
 * Main entry point for accessing Stylish sources.
 */
interface DiaryDataSource {

    suspend fun getUsersDiarys(startTime:Long , endTime: Long): Result<List<Diary>>

    suspend fun postDiary(diarys:Diary): Result<Boolean>

    fun getLiveDiary(startTime:Long , endTime: Long): MutableLiveData<List<Diary>>

    suspend fun getStore(): Result<List<Stores>>

    fun getLiveStore(): MutableLiveData<List<Stores>>

    suspend fun queryDiaryCount(): Result<Int>

    suspend fun pushProfile(user: User): Result<Boolean>

    suspend fun queryStoreHistory(storeName:String): Result<List<Diary>>
}
