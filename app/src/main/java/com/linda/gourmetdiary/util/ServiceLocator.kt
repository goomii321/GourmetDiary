package com.linda.gourmetdiary.util

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.linda.gourmetdiary.data.source.DefaultDiaryRepository
import com.linda.gourmetdiary.data.source.DiaryDataSource
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.data.source.local.DiaryLocalDataSource
import com.linda.gourmetdiary.data.source.remote.DiaryRemoteDataSource

/**
 * A Service Locator for the [Repository].
 */
object ServiceLocator {

    @Volatile
    var diaryRepository: DiaryRepository? = null
        @VisibleForTesting set

    fun provideTasksRepository(context: Context): DiaryRepository {
        synchronized(this) {
            return diaryRepository
                ?: diaryRepository
                ?: createStylishRepository(context)
        }
    }

    private fun createStylishRepository(context: Context): DiaryRepository {
        return DefaultDiaryRepository(DiaryRemoteDataSource,
            createLocalDataSource(context)
        )
    }

    private fun createLocalDataSource(context: Context): DiaryDataSource {
        return DiaryLocalDataSource(context)
    }
}