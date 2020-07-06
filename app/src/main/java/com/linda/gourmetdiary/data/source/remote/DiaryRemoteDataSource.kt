package com.linda.gourmetdiary.data.source.remote

import android.icu.util.Calendar
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.data.Result
import com.linda.gourmetdiary.data.Users
import com.linda.gourmetdiary.data.source.DiaryDataSource
import com.linda.gourmetdiary.util.Logger
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Created by Wayne Chen in Jul. 2019.
 *
 * Implementation of the Stylish source that from network.
 */
object DiaryRemoteDataSource : DiaryDataSource {

    private const val PATH_USERS = "Users"
    private const val PATH_STORESS = "Stores"
    private const val PATH_DIARYS = "diarys"
    private const val KEY_CREATED_TIME = "createdTime"

    override suspend fun getUsersDiarys(): Result<List<Users>> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
//            .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
            .document("G1P80SW55MbkixdY69cx")
            .collection(PATH_DIARYS)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<Users>()
                    for (document in task.result!!) {
                        Logger.d(document.id +  "=>"  + document.data)

                        val article = document.toObject(Users::class.java)
                        list.add(article)
                    }
                    continuation.resume(Result.Success(list))
                } else {
                    task.exception?.let {

                        Logger.d("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(DiaryApplication.instance.getString(R.string.ng_msg)))
                }
            }
    }

    override suspend fun postDiary(diarys: Diary): Result<Boolean> = suspendCoroutine { continuation ->
        val diary = FirebaseFirestore.getInstance().collection(PATH_USERS).document("G1P80SW55MbkixdY69cx")
            .collection(PATH_DIARYS)
        val document = diary.document()

        diarys.diaryId = document.id
        diarys?.createdTime = Calendar.getInstance().timeInMillis.toString()

        document
            .set(diarys)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("Publish: $diarys")

                    continuation.resume(Result.Success(true))
                } else {
                    task.exception?.let {

                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(DiaryApplication.instance.getString(R.string.ng_msg)))
                }
            }
    }
}
