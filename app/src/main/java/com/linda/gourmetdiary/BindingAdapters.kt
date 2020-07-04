package com.linda.gourmetdiary

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import app.appworks.school.publisher.network.LoadApiStatus
import com.linda.gourmetdiary.data.Users

/**
 * Created by Wayne Chen on 2020-01-15.
 */
@BindingAdapter("users")
fun bindRecyclerView(recyclerView: RecyclerView, homeItems: List<Users>?) {
    homeItems?.let {
        recyclerView.adapter?.apply {
            when (this) {
//                is HomeAdapter -> submitList(it)
            }
        }
    }
}

//@BindingAdapter("timeToDisplayFormat")
//fun bindDisplayFormatTime(textView: TextView, time: Long?) {
//    textView.text = time?.toDisplayFormat()
//}


/**
 * According to [LoadApiStatus] to decide the visibility of [ProgressBar]
 */
@BindingAdapter("setupApiStatus")
fun bindApiStatus(view: ProgressBar, status: LoadApiStatus?) {
    when (status) {
        LoadApiStatus.LOADING -> view.visibility = View.VISIBLE
        LoadApiStatus.DONE, LoadApiStatus.ERROR -> view.visibility = View.GONE
    }
}

/**
 * According to [message] to decide the visibility of [ProgressBar]
 */
@BindingAdapter("setupApiErrorMessage")
fun bindApiErrorMessage(view: TextView, message: String?) {
    when (message) {
        null, "" -> {
            view.visibility = View.GONE
        }
        else -> {
            view.text = message
            view.visibility = View.VISIBLE
        }
    }
}