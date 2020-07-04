package com.linda.gourmetdiary

import android.app.Application
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.util.ServiceLocator
import kotlin.properties.Delegates

/**
 * Created by Wayne Chen in Jul. 2019.
 *
 * An application that lazily provides a repository. Note that this Service Locator pattern is
 * used to simplify the sample. Consider a Dependency Injection framework.
 *
 * you have to add android:name= .DiaryApplication in AndroidManifest.
 */
class DiaryApplication : Application() {

    // Depends on the flavor,
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
