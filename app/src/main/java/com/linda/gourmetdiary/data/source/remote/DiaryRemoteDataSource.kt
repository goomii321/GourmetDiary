package com.linda.gourmetdiary.data.source.remote

import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.data.*
import com.linda.gourmetdiary.data.source.DiaryDataSource
import com.linda.gourmetdiary.util.Logger
import com.linda.gourmetdiary.util.UserManager
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object DiaryRemoteDataSource : DiaryDataSource {

    private const val PATH_USERS = "Users"
    private const val PATH_STORES = "Stores"
    private const val PATH_DIARYS = "diarys"
    private const val KEY_CREATED_TIME = "createdTime"
    private const val KEY_EATING_TIME = "eatingTime"
    private const val KEY_STORE_NAME = "store.storeName"
    private const val KEY_OF_STORE_BRANCH = "store.storeBranch"
    private const val KEY_FOOD_NAME = "food.foodName"
    private const val KEY_STORES_NAME = "storeName"
    private const val KEY_STORE_BRANCH = "storeBranch"

    //week offset: 6,604,740,000
    override suspend fun getUsersDiarys(startTime:Long , endTime: Long): Result<List<Diary>> = suspendCoroutine { continuation ->

        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(UserManager.userId ?: "")
            .collection(PATH_DIARYS)
            .whereGreaterThanOrEqualTo(KEY_EATING_TIME,startTime)
            .whereLessThanOrEqualTo(KEY_EATING_TIME,endTime)
            .orderBy(KEY_EATING_TIME,Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<Diary>()
                    for (document in task.result!!) {
                        Logger.i("getUsersDiarys: " + document.data)
                        val diary = document.toObject(Diary::class.java)
                        list.add(diary)
                    }
                    continuation.resume(Result.Success(list))
                } else {
                    task.exception?.let {
                        Logger.d("[${this::class.simpleName}] Error getting documents. ${it.message}")
                    }
                }
            }

        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(UserManager.userId ?: "")
            .get().addOnSuccessListener {task2 ->
                if (task2 != null){
                    UserManager.userData.signUpDate = task2.getLong("signUpDate")
                    Logger.d("signUpDate = ${UserManager.userData.signUpDate}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override suspend fun postDiary(diarys: Diary): Result<Boolean> =
        suspendCoroutine { continuation ->
            val diary = FirebaseFirestore.getInstance().collection(PATH_USERS)
                .document(UserManager.userId ?: "")
                .collection(PATH_DIARYS)
            val stores = FirebaseFirestore.getInstance().collection(PATH_USERS)
                .document(UserManager.userId ?: "").collection(PATH_STORES)

            val document = diary.document()

            diarys.diaryId = document.id
            diarys.createdTime = Calendar.getInstance().timeInMillis
            diarys.store?.updateTime = Calendar.getInstance().timeInMillis
            Logger.d("update time is ${diarys.store?.updateTime}")

            diarys.store?.let {
                stores.whereEqualTo(KEY_STORES_NAME,"${it.storeName}").whereEqualTo(KEY_STORE_BRANCH,"${it.storeBranch}")
                    .get()
                    .addOnSuccessListener { task ->
                        Logger.d("storeTask whereEqualTo = ${task.documents}; ${it.storeName}")
                        if (task.isEmpty) {
                            Logger.d("whereEqualTo is Empty")
                            stores.document("${it.storeName}").set(it)
                                .addOnCompleteListener {task ->
                                if (task.isSuccessful) {
                                    Logger.i("store task : ${task.result}")
//                                    continuation.resume(Result.Success(true))
                                } else {
                                    task.exception?.let {
                                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                                        continuation.resume(Result.Error(it))
                                        return@addOnCompleteListener
                                    }
                                    continuation.resume(Result.Fail(DiaryApplication.instance.getString(R.string.ng_msg)))
                                }
                            }

                        } else {
                            document.update("updateTime",it.updateTime, "storeImage",it.storeImage, "storeLocationId",it.storeLocationId)
                        }
                    }
            }
            document.set(diarys)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
//                        Logger.i("diary: $diarys")
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

    override fun getLiveDiary(startTime:Long , endTime: Long): MutableLiveData<List<Diary>> {
        val liveData = MutableLiveData<List<Diary>>()

        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(UserManager.userId ?: "")
            .collection(PATH_DIARYS)
            .whereGreaterThanOrEqualTo(KEY_EATING_TIME,startTime)
            .whereLessThanOrEqualTo(KEY_EATING_TIME,endTime)
            .orderBy(KEY_EATING_TIME, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->

                Logger.i("addSnapshotListener detect")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                val list = mutableListOf<Diary>()
                for (document in snapshot!!) {
                    Logger.d(document.id + " => " + document.data)

                    val liveDiary = document.toObject(Diary::class.java)
                    list.add(liveDiary)
                }

                liveData.value = list
            }
        return liveData
    }

    override suspend fun getStore(): Result<List<Stores>> = suspendCoroutine { continuation ->

        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(UserManager.userId ?: "")
            .collection(PATH_STORES)
            .orderBy("updateTime",Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<Stores>()
                    for (document in task.result!!) {
                        Logger.d(document.id + "=>" + document.data)

                        val store = document.toObject(Stores::class.java)
                        list.add(store)
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

    override fun getLiveStore(): MutableLiveData<List<Stores>> {
        val liveData = MutableLiveData<List<Stores>>()

        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(UserManager.userId ?: "")
            .collection(PATH_STORES)
            .orderBy("updateTime",Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->

                Logger.i("addSnapshotListener detect, $snapshot")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                val list = mutableListOf<Stores>()
                for (document in snapshot!!) {
                    Logger.d(document.id + " => " + document.data)

                    val liveDiary = document.toObject(Stores::class.java)
                    list.add(liveDiary)
                }

                liveData.value = list
            }
        return liveData
    }

    override suspend fun queryDiaryCount(): Result<Int> = suspendCoroutine { continuation ->

        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(UserManager.userId ?: "")
            .collection(PATH_DIARYS)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var count = 0
                    if (task.result?.count() != 0) {
                        count = task.result?.size()!!
                        Logger.d("success $count, ${task.result}")
                    }
                    continuation.resume(Result.Success(count))
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

    override suspend fun queryStoreCount(): Result<Int> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(UserManager.userId ?: "")
            .collection(PATH_STORES)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var count = 0
                    if (task.result?.count() != 0) {
                        count = task.result?.size()!!
                        Logger.d("queryStoreCount success $count, ${task.result}")
                    }
                    continuation.resume(Result.Success(count))
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

    override suspend fun pushProfile(user: User): Result<Boolean>  =
        suspendCoroutine { continuation ->
            val userdata = FirebaseFirestore.getInstance().collection(PATH_USERS)
            var document = userdata.document("${UserManager.userData.userId}")

            userdata.whereEqualTo("userId",UserManager.userData.userId)
                .get().addOnSuccessListener { task ->
                    if (task.isEmpty){
                        Logger.i("task = $task")
                        user.signUpDate = Calendar.getInstance().timeInMillis
                        document.set(user)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Logger.i("user: $user")
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
                    } else {
                        document.update("userId",UserManager.userData.userId)
                    }
                }


        }

    override suspend fun queryStoreHistory(storeName:String, storeBranch:String): Result<List<Diary>> = suspendCoroutine { continuation ->

        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(UserManager.userId ?: "")
            .collection(PATH_DIARYS)
            .whereEqualTo(KEY_STORE_NAME,storeName)
            .whereEqualTo( KEY_OF_STORE_BRANCH,storeBranch)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<Diary>()
                    for (document in task.result!!) {
                        Logger.i("queryStoreHistory: " + document.data)
                        val diary = document.toObject(Diary::class.java)
                        list.add(diary)
                    }
                    continuation.resume(Result.Success(list))
                } else {
                    task.exception?.let {
                        Logger.d("[${this::class.simpleName}] Error getting documents. ${it.message}")
                    }
                }
            }
    }

    override suspend fun queryReminder(): Result<List<Diary>> = suspendCoroutine { continuation ->

        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(UserManager.userId ?: "")
            .collection(PATH_DIARYS)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<Diary>()
                    for (document in task.result!!) {
                        Logger.d(document.id + "=>" + document.data)
                        val diary = document.toObject(Diary::class.java)
                        list.add(diary)
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

    override suspend fun searchTemplate(searchWord: String): Result<List<Diary>>  = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(UserManager.userId ?: "")
            .collection(PATH_DIARYS).orderBy(KEY_FOOD_NAME)
            .startAt(searchWord).get()
            .addOnCompleteListener {task ->
                if (task.isSuccessful){
                    val list = mutableListOf<Diary>()
                    for (document in task.result!!) {
                        Logger.i("searchTemplate: " + document.data)
                        val diary = document.toObject(Diary::class.java)
                        list.add(diary)
                    }
                    continuation.resume(Result.Success(list))
                } else {

                }
            }
    }
}
