package com.linda.gourmetdiary.util

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.data.User

object UserManager {

    private const val USER_ID = "userId"

    var userData = User()

    var userId: String?
        get() {
            val userId = DiaryApplication.instance.getSharedPreferences(
                USER_ID, Context.MODE_PRIVATE)
                .getString(USER_ID, null)
            return userId
        }
        set(value) {
            val editor = DiaryApplication.instance.getSharedPreferences(
                USER_ID, Context.MODE_PRIVATE).edit()
            editor.putString(USER_ID, value).apply()
        }

    val isLoggedIn: Boolean
        get() = userData.userId != null
}