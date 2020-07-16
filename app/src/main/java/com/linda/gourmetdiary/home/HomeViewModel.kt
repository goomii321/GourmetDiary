package com.linda.gourmetdiary.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linda.gourmetdiary.network.LoadApiStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.data.Result
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.util.Logger
import com.linda.gourmetdiary.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val isLoggedIn
        get() = UserManager.isLoggedIn

    init {

    }

}


//        var users = FirebaseFirestore.getInstance().collection("Users").document("G1P80SW55MbkixdY69cx")
//            .collection("diarys")
//
//        users.get().addOnCompleteListener { task ->
//
//            if (task.isSuccessful) {
//                for (document in task.result!!) {
//
//                        Log.d(
//                            "getUsersResult",
//                            document.id + " => " + document.data)

//                    title.value= document.getString("title")
//                    tag.value = document.getString("tag")
//                    id.value = document.getString("id")
//                    createdTime.value = document.getLong("createdTime")
//                    content.value = document.getString("content")
//
//                    email.value = document.getString("author.email")
//                    authorId.value= document.getString("author.id")
//                    name.value = document.getString("author.name")
//
//                    val author = Author("${email.value}", "${authorId.value}", "${name.value}")
//                    val articleData = Article(listOf(author),"${name.value}","${content.value}",
//                        "${createdTime.value}", "${id.value}", "${tag.value}",
//                        "${title.value}")
//
//                    list.add(articleData)
//                    mutableData.value = list
//                        Log.i("linda ", "mutableData = ${author}")
//                }

//            } else {
//                Log.i("All data", "NO DATA ERROR, ${task.exception}")
//            }
//        }
//
