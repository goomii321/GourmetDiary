package com.linda.gourmetdiary

import android.icu.text.SimpleDateFormat
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.network.LoadApiStatus
import com.linda.gourmetdiary.data.Users
import com.linda.gourmetdiary.diarys.DiarysAdapter
import java.util.*

@BindingAdapter("diary")
fun bindRecyclerView(recyclerView: RecyclerView, diary: List<Diary>?) {
    diary?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is DiarysAdapter -> submitList(it)
            }
        }
    }
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.common_google_signin_btn_icon_dark_normal)
                    .error(R.drawable.common_google_signin_btn_icon_dark_normal))
            .into(imgView)
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@BindingAdapter("timeToDisplayFormat")
fun bindDisplayFormatTime(textView: TextView, time: Long?) {
    textView.text = time?.toDisplayFormat()
}

@RequiresApi(Build.VERSION_CODES.N)
fun Long.toDisplayFormat(): String {
    return SimpleDateFormat("yyyy.MM.dd hh:mm", Locale.TAIWAN).format(this)
}


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