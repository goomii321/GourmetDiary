package com.linda.gourmetdiary.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Diarys4Day(
    var dayTitle: Long? = -1L,
    var diarys: MutableList<Diary> = mutableListOf()
) : Parcelable
