package com.linda.gourmetdiary.data.source.local

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.data.Result
import com.linda.gourmetdiary.data.Stores
import com.linda.gourmetdiary.data.Users
import com.linda.gourmetdiary.data.source.DiaryDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by Wayne Chen in Jul. 2019.
 *
 * Concrete implementation of a Stylish source as a db.
 */
class DiaryLocalDataSource(val context: Context) : DiaryDataSource {
    override suspend fun getUsersDiarys(): Result<List<Diary>> {
        TODO("Not yet implemented")
    }

    override suspend fun postDiary(diarys: Diary): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getLiveDiary(): MutableLiveData<List<Diary>> {
        TODO("Not yet implemented")
    }

    override suspend fun getStore(): Result<List<Stores>> {
        TODO("Not yet implemented")
    }
}
