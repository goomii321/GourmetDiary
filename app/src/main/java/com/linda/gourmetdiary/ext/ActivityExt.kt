package com.linda.gourmetdiary.ext

import android.app.Activity
import android.view.Gravity
import android.widget.Toast
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.factory.ViewModelFactory

fun Activity.getVmFactory(): ViewModelFactory {
    val repository = (applicationContext as DiaryApplication).diaryRepository
    return ViewModelFactory(repository)
}

fun Activity?.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}