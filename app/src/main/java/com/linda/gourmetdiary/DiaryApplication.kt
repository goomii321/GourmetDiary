package com.linda.gourmetdiary

import android.app.Application
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.util.ServiceLocator
import kotlin.properties.Delegates

class DiaryApplication : Application() {

    val diaryRepository: DiaryRepository
        get() = ServiceLocator.provideTasksRepository(this)

    companion object {
        var instance: DiaryApplication by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun isLiveDataDesign() = true
}
