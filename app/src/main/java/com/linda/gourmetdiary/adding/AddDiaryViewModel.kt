package com.linda.gourmetdiary.adding

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.appworks.school.publisher.network.LoadApiStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.data.Diarys
import com.linda.gourmetdiary.data.Users
import com.linda.gourmetdiary.data.Result
import com.linda.gourmetdiary.data.source.DiaryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class AddDiaryViewModel(private val repository: DiaryRepository,
                        private val users: Users?) : ViewModel() {

    private val _user = MutableLiveData<Users>().apply {
        value = Users(
            diarys = Diarys()
        )
    }
    val user: LiveData<Users>
        get() = _user

    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    var foodStatus = MutableLiveData<Boolean>().apply {
        value = false
    }

    var storeStatus = MutableLiveData<Boolean>().apply {
        value = false
    }

//    var diary = FirebaseFirestore.getInstance().collection("Users")
//        .document("G1P80SW55MbkixdY69cx").collection("diarys")
//    val document = diary.document()

//    val foodName = MutableLiveData<String>()
//    val foodCombo = MutableLiveData<String>()
//    val foodContent = MutableLiveData<String>()
//    val nextTimeReminder = MutableLiveData<String>()
    val foodRating = MutableLiveData<Int>()
    val healthyScore = MutableLiveData<Int>()
//    val price = MutableLiveData<String>()
//    val eatingTime = MutableLiveData<String>().apply { value= "12:30" }
//    val tag = MutableLiveData<String>().apply { value = "速食" }

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun setFoodVisible() {
        foodStatus.value = !foodStatus.value!!
    }

    fun setStoreVisible() {
        storeStatus.value = !storeStatus.value!!
    }

    fun addData(users: Users) {
        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.postDiary(users)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                }
                else -> {
                    _error.value = DiaryApplication.instance.getString(R.string.ng_msg)
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }
}
//val data = hashMapOf(
//    "food" to hashMapOf(
//        "foodName" to "${foodName.value}",
//        "foodCombo" to "${foodCombo.value}",
//        "foodRate" to "${foodRating.value}" ,
//        "healthyScore" to "${healthyScore.value}" ,
//        "eatingTime" to "${eatingTime.value}" ,
//        "price" to "${price.value}",
//        "tag" to "${tag.value}",
//        "foodContent" to "${foodContent.value}",
//        "nextTimeReminder" to "${nextTimeReminder.value}"
//    ), "store" to hashMapOf(
//        "storeName" to "${foodName.value}",
//        "storeBooking" to "${foodCombo.value}",
//        "storeBranch" to "${foodRating.value}" ,
//        "storeHtml" to "${healthyScore.value}" ,
//        "storeLocation" to "${eatingTime.value}" ,
//        "storeMinOrder" to "${price.value}",
//        "storeOpenTime" to "${tag.value}",
//        "storePhone" to "${foodContent.value}"
//    ),
//    "createdTime" to Calendar.getInstance().timeInMillis,
//    "diaryId" to document.id ,
//    "mainImage" to "mainImage",
//    "images" to listOf<String>("01","02","03"),
//    "type" to "午餐"
//)
//document.set(data)
//Log.i("addData","title= ${foodName.value} , tag= ${foodContent.value}, content= ${price.value}")