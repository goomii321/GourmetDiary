package com.linda.gourmetdiary.data.source

import androidx.lifecycle.LiveData
import com.linda.gourmetdiary.data.Result
import com.linda.gourmetdiary.data.Users


/**
 * Created by Wayne Chen in Jul. 2019.
 *
 * Interface to the Stylish layers.
 */
interface DiaryRepository {

    suspend fun getUsersDiarys(): Result<List<Users>>


}
