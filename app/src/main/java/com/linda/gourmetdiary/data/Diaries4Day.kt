package com.linda.gourmetdiary.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Diaries4Day(
    var dayTitle: Long? = -1L,
    var diaries: MutableList<Diary> = mutableListOf()
) : Parcelable
