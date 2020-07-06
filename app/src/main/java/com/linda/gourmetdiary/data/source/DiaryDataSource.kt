package com.linda.gourmetdiary.data.source

import androidx.lifecycle.LiveData
import com.linda.gourmetdiary.data.Result
import com.linda.gourmetdiary.data.Users

/**
 * Created by Wayne Chen in Jul. 2019.
 *
 * Main entry point for accessing Stylish sources.
 */
interface DiaryDataSource {

    suspend fun getUsersDiarys(): Result<List<Users>>

    suspend fun postDiary(users:Users): Result<Boolean>
}
