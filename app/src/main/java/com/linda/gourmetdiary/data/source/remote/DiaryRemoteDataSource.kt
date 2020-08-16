package com.linda.gourmetdiary.data.source.remote

import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.data.*
import com.linda.gourmetdiary.data.source.DiaryDataSource
import com.linda.gourmetdiary.util.Logger
import com.linda.gourmetdiary.util.UserManager
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object DiaryRemoteDataSource : DiaryDataSource {

    private const val PATH_USERS = "Users"
    private const val PATH_STORES = "Stores"
    private const val PATH_DIARIES = "diarys"
    private const val KEY_EATING_TIME = "eatingTime"
    private const val KEY_STORE_NAME = "store.storeName"
    private const val KEY_OF_STORE_BRANCH = "store.storeBranch"
    private const val KEY_FOOD_NAME = "food.foodName"
    private const val KEY_STORES_NAME = "storeName"
    private const val KEY_STORE_BRANCH = "storeBranch"
    private const val SIGN_UP_DATE = "signUpDate"
    private const val UPDATE_TIME = "updateTime"
    private const val STORE_IMAGE = "storeImage"
    private const val STORE_LOCATION_ID = "storeLocationId"
    private const val USER_ID = "userId"

    //week offset: 6,604,740,000
    override suspend fun getDiaries(startTime:Long, endTime: Long): Result<List<Diary>> = suspendCoroutine { continuation ->

        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(UserManager.userId ?: "")
            .collection(PATH_DIARIES)
            .whereGreaterThanOrEqualTo(KEY_EATING_TIME,startTime)
            .whereLessThanOrEqualTo(KEY_EATING_TIME,endTime)
            .orderBy(KEY_EATING_TIME,Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<Diary>()
                    task.result?.forEach { document ->
//                      Logger.i("getUsersDiarys: " + document.data)
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
            .get().addOnSuccessListener {signUpDate ->
                if (signUpDate != null){
                    UserManager.userData.signUpDate = signUpDate.getLong(SIGN_UP_DATE)
//                    Logger.d("signUpDate = ${UserManager.userData.signUpDate}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override suspend fun postDiary(diaries: Diary): Result<Boolean> =
        suspendCoroutine { continuation ->
            val diary = FirebaseFirestore.getInstance().collection(PATH_USERS)
                .document(UserManager.userId ?: "")
                .collection(PATH_DIARIES)
            val stores = FirebaseFirestore.getInstance().collection(PATH_USERS)
                .document(UserManager.userId ?: "").collection(PATH_STORES)

            diaries.diaryId = diary.document().id
            diaries.createdTime = Calendar.getInstance().timeInMillis
            diaries.store?.updateTime = Calendar.getInstance().timeInMillis
//            Logger.d("update time is ${diarys.store?.updateTime}")

            diaries.store?.let {
                stores.whereEqualTo(KEY_STORES_NAME,"${it.storeName}").whereEqualTo(KEY_STORE_BRANCH,"${it.storeBranch}")
                    .get()
                    .addOnSuccessListener { task ->
//                        Logger.d("storeTask whereEqualTo = ${task.documents}; ${it.storeName}")
                        if (task.isEmpty) {
//                            Logger.d("whereEqualTo is Empty")
                            stores.document("${it.storeName}").set(it)
                                .addOnCompleteListener {task ->
                                if (task.isSuccessful) {
//                                    Logger.i("store task : ${task.result}")
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
                            stores.document("${it.storeName}").update(UPDATE_TIME,it.updateTime)
                        }
                    }
            }
            diary.document().set(diaries)
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

    override suspend fun updateStoreImage(store: Store): Result<Boolean> = suspendCoroutine { continuation ->
        val stores = FirebaseFirestore.getInstance().collection(PATH_USERS)
            .document(UserManager.userId ?: "").collection(PATH_STORES)

        store.let {
            stores.whereEqualTo(KEY_STORES_NAME,"${it.storeName}").whereEqualTo(KEY_STORE_BRANCH,"${it.storeBranch}")
                .get()
                .addOnSuccessListener { task ->
                    Logger.d("updateStoreImage storeTask whereEqualTo = ${task.documents}; ${it.storeName}")
                    if (task.isEmpty) {
                        Logger.d("Store Not Found")

                    } else {
                        stores.document("${it.storeName}").update(STORE_IMAGE,it.storeImage)
                        Logger.d("updateStoreImage image = ${it.storeImage}")
                        continuation.resume(Result.Success(true))
                    }
                }
        }
    }

    override fun getLiveDiary(startTime:Long , endTime: Long): MutableLiveData<List<Diary>> {
        val liveData = MutableLiveData<List<Diary>>()

        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(UserManager.userId ?: "")
            .collection(PATH_DIARIES)
            .whereGreaterThanOrEqualTo(KEY_EATING_TIME,startTime)
            .whereLessThanOrEqualTo(KEY_EATING_TIME,endTime)
            .orderBy(KEY_EATING_TIME, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->

//                Logger.i("addSnapshotListener detect")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                val list = mutableListOf<Diary>()
                snapshot?.forEach { document ->
//                  Logger.d(document.id + " => " + document.data)

                    val liveDiary = document.toObject(Diary::class.java)
                    list.add(liveDiary)
                }

                liveData.value = list
            }
        return liveData
    }

    override suspend fun getStore(): Result<List<Store>> = suspendCoroutine { continuation ->

        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(UserManager.userId ?: "")
            .collection(PATH_STORES)
            .orderBy(UPDATE_TIME,Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<Store>()
                    task.result?.forEach { document ->
//                      Logger.d(document.id + "=>" + document.data)
                        val store = document.toObject(Store::class.java)
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

    override fun getLiveStore(): MutableLiveData<List<Store>> {

        val liveData = MutableLiveData<List<Store>>()

        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(UserManager.userId ?: "")
            .collection(PATH_STORES)
            .orderBy(UPDATE_TIME,Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->

//                Logger.i("addSnapshotListener detect, $snapshot")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                val list = mutableListOf<Store>()
                snapshot?.forEach { document ->
//                    Logger.d(document.id + " => " + document.data)
                    val liveDiary = document.toObject(Store::class.java)
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
            .collection(PATH_DIARIES)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var count = 0
                    task.result?.let {
                        if (it.count() != 0) {
                            count = it.size()
//                        Logger.d("success $count, ${task.result}")
                        }
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
                    task.result?.let {
                        if (it.count() != 0) {
                            count = it.size()
//                        Logger.d("queryStoreCount success $count, ${task.result}")
                        }
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

            userdata.whereEqualTo(USER_ID,UserManager.userData.userId)
                .get().addOnSuccessListener { task ->
                    if (task.isEmpty){
//                        Logger.i("task = $task")
                        user.signUpDate = Calendar.getInstance().timeInMillis
                        document.set(user)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
//                                    Logger.i("user: $user")
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
                        document.update(USER_ID,UserManager.userData.userId)
                    }
                }


        }

    override suspend fun queryStoreHistory(storeName:String, storeBranch:String): Result<List<Diary>> = suspendCoroutine { continuation ->

        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(UserManager.userId ?: "")
            .collection(PATH_DIARIES)
            .whereEqualTo(KEY_STORE_NAME,storeName)
            .whereEqualTo( KEY_OF_STORE_BRANCH,storeBranch)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<Diary>()
                    task.result?.forEach { document ->
//                        Logger.i("queryStoreHistory: " + document.data)
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
            .collection(PATH_DIARIES)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<Diary>()
                    task.result?.forEach { document ->
//                        Logger.d(document.id + "=>" + document.data)
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
            .collection(PATH_DIARIES).orderBy(KEY_FOOD_NAME)
            .startAt(searchWord).get()
            .addOnCompleteListener {task ->
                if (task.isSuccessful){
                    val list = mutableListOf<Diary>()
                    task.result?.forEach { document ->
//                      Logger.i("searchTemplate: " + document.data)
                        val diary = document.toObject(Diary::class.java)
                        list.add(diary)
                    }
                    continuation.resume(Result.Success(list))
                }
            }
    }

    override suspend fun uploadImage(uri: Uri): Result<String> = suspendCoroutine { continuation ->
        var firstPhoto = true
        val filename = UUID.randomUUID().toString()
        val image = MutableLiveData<String>()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        uri.let { uri ->
            ref.putFile(uri)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        ref.downloadUrl.addOnSuccessListener {
                            image.value = it.toString()
                            if (firstPhoto) {
                                firstPhoto = false
                            }
                            Logger.d("Image = ${image.value}")
                            continuation.resume(Result.Success(image.value.toString()))
                        }
                    }
                    Logger.d("error = ${it.exception}")
                }
        }
    }
}
