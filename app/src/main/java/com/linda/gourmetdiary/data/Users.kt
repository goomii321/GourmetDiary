package com.linda.gourmetdiary.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Users(
    var diarys: Diary? = null,
    var profile: Profile? = null

) : Parcelable

@Parcelize
data class Profile(
    var token: String? = "",
    var userId: String? = "",
    var userName: String? = "",
    var userPhoto: String? = "",
    var userEmail: String? = ""
) : Parcelable

@Parcelize
data class Diary(
    var diaryId: String? = "",
    var mainImage: String? = "",
    var images: List<String>? = listOf("https://cdn.pixabay.com/photo/2013/07/13/09/37/taco-155812_960_720.png"),
    var createdTime: Long? = 0,
    var type: String? = "",
    val store: Store? = null,
    var food: Food? = null,
    var eatingTime: Long? = 0
) : Parcelable {

}

@Parcelize
data class Store(
    var storeId: String? = "",
    var storeName: String? = "",
    var storePhone: String? ="",
    var storeBooking: Boolean? = null,
    var storeBranch: String? = "無",
    var storeHtml: String? = "www",
    var storeLocation: String? = "e34",
    var storeMinOrder: String? = "300",
    var storeOpenTime: String? = "7:00"
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

