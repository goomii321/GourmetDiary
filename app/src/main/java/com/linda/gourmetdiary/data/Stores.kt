package com.linda.gourmetdiary.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Stores(
    var updateTime: Long? = -1,
    var storeName: String? = "",
    var storePhone: String? ="",
    var storeBooking: Boolean? = null,
    var storeBranch: String? = "",
    var storeHtml: String? = "",
    var storeLocationId: String? = "",
    var storeLocation: String? = "",
    var storeMinOrder: String? = "",
    var storeOpenTime: String? = "",
    var storeImage: String? = ""
) : Parcelable

