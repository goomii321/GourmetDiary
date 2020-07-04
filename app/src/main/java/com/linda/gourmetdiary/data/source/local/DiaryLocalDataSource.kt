package com.linda.gourmetdiary.data.source.local

import android.content.Context
import androidx.lifecycle.LiveData
import com.linda.gourmetdiary.data.Result
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
    override suspend fun getUsersDiarys(): Result<List<Users>> {
        TODO("Not yet implemented")
    }
}
