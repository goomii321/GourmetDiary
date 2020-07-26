package com.linda.gourmetdiary.data

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var userId: String? = "",
    var userName: String? = "",
    var userPhoto: String? = "",
    var userEmail: String? = "",
    var signUpDate: Long? = -1
) : Parcelable

@Parcelize
data class Diary(
    var diaryId: String? = "",
    var mainImage: String? = "",
    var images: List<String>? = listOf("https://cdn.pixabay.com/photo/2013/07/13/09/37/taco-155812_960_720.png"),
    var createdTime: Long? = -1,
    var type: String? = "早餐",
    val store: Store? = null,
    var food: Food? = null,
    var eatingTime: Long? = null
) : Parcelable {

}

@Parcelize
data class Store(
    var updateTime: Long? = -1,
    var storeName: String? = "",
    var storePhone: String? ="無",
    var storeBooking: Boolean? = false ,
    var storeBranch: String? = "無",
    var storeHtml: String? = "無",
    var storeLocation: String? = "無",
    var storeMinOrder: String? = "無",
    var storeOpenTime: String? = "無",
    var storeLocationId: String? = "",
    var storeImage: String? = ""
) : Parcelable

@Parcelize
data class Food(
    var foodId: String? = "",
    var foodName: String? = "",
    var foodCombo: String? = "",
    var foodContent: String? = "",
    var foodRate: Int? = 0,
    var healthyScore: Int? = 0,
    var nextTimeRemind: String? = "",
    var price: String? = "",
    var tag: String? = ""
) : Parcelable

