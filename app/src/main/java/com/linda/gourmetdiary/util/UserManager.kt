package com.linda.gourmetdiary.util

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.data.User

object UserManager {

    private const val USER_ID = "userId"
    private const val USER_NAME = "userName"
    private const val USER_PHOTO = "userPhoto"

    var userData = User()

    var diaries: List<Diary> = listOf()

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

//    var userName: String?
//        get() {
//            val userName = DiaryApplication.instance.getSharedPreferences(
//                USER_NAME, Context.MODE_PRIVATE)
//                .getString(USER_NAME, null)
//            return userName
//        }
//        set(value) {
//            val editor = DiaryApplication.instance.getSharedPreferences(
//                USER_ID, Context.MODE_PRIVATE).edit()
//            editor.putString(USER_NAME, value).apply()
//        }
//
//    var userPhoto: String?
//        get() {
//            val userPhoto = DiaryApplication.instance.getSharedPreferences(
//                USER_PHOTO, Context.MODE_PRIVATE)
//                .getString(USER_PHOTO, null)
//            return userPhoto
//        }
//        set(value) {
//            val editor = DiaryApplication.instance.getSharedPreferences(
//                USER_PHOTO, Context.MODE_PRIVATE).edit()
//            editor.putString(USER_PHOTO, value).apply()
//        }

    val isLoggedIn: Boolean
        get() = userData.userId != null
}