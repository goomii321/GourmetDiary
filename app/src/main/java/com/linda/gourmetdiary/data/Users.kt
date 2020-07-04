package com.linda.gourmetdiary.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Users(
    var diarys: Diarys? = null,
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
data class Diarys(
    var diaryId: String? = "",
    var mainImage: String? = "",
    var images: String? = "",
    var type: String? = "",
    val store: Store? = null,
    val food: Food? = null

) : Parcelable

@Parcelize
data class Store(
    var storeId: String? = "",
    var storeName: String? = "",
    var storePhone: String? ="",
    var storeBooking: Boolean? = null,
    var storeBranch: String? = "",
    var storeHtml: String? = "",
    var storeLocation: String? = "",
    var storeMinOrder: String? = "",
    var storeOpenTime: String? = ""
) : Parcelable

@Parcelize
data class Food(
    var foodId: String? = "",
    var foodName: String? = "",
    var foodCombo: String? = "",
    var foodContent: String? = "",
    var foodRate: Long? = 0,
    var healthyScore: Long? = 0,
    var nextTimeRemind: String? = "",
    var price: Long? = 0,
    var tag: String? = ""
) : Parcelable

