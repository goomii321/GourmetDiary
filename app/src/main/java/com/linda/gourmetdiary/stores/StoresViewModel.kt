package com.linda.gourmetdiary.stores

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.linda.gourmetdiary.data.source.DiaryRepository

class StoresViewModel(private val repository: DiaryRepository) : ViewModel() {

    init {
        getStoresResult()
    }
    private fun getStoresResult() {

        var stores = FirebaseFirestore.getInstance().collection("Stores")
        stores.get().addOnCompleteListener {task ->

            if (task.isSuccessful) {
                for (document in task.result!!) {
                    Log.d(
                        "ALLData",
                        document.id + " => " + document.data)
                }
            } else {
                Log.i("getStoresResult", "NO DATA ERROR, ${task.exception}")
            }
        }
    }
}
