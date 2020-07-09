package com.linda.gourmetdiary.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LogIn (
    var userName: String? ="",
    var userPhoto: String? = ""
) : Parcelable