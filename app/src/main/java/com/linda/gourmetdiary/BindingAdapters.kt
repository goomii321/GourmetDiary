package com.linda.gourmetdiary

import android.annotation.SuppressLint
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
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.data.Store
import com.linda.gourmetdiary.diaries.DailyItemAdapter
import com.linda.gourmetdiary.diaries.DataItem
import com.linda.gourmetdiary.diaries.DiariesAdapter
import com.linda.gourmetdiary.diaries.detail.DiaryGalleryAdapter
import com.linda.gourmetdiary.home.HomeAdapter
import com.linda.gourmetdiary.network.LoadApiStatus
import com.linda.gourmetdiary.stores.StoresAdapter
import com.linda.gourmetdiary.stores.detail.StoreDetailAdapter
import com.linda.gourmetdiary.template.SearchAdapter


@BindingAdapter("diary")
fun bindRecyclerView(recyclerView: RecyclerView, diary: List<Diary>?) {
    diary?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is DailyItemAdapter -> submitList(it)
                is StoreDetailAdapter -> submitList(it)
                is SearchAdapter -> submitList(it)
                is HomeAdapter ->submitList(it)
            }
        }
    }
}

@BindingAdapter("store")
fun bindStoreRecyclerView(recyclerView: RecyclerView, store: List<Store>?) {
    store?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is StoresAdapter -> submitList(it)
            }
        }
    }
}

@BindingAdapter("dataItems")
fun bindRecyclerViewWithHomeItems(recyclerView: RecyclerView, dataItems: List<DataItem>?) {
    dataItems?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is DiariesAdapter -> submitList(it)
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
//            .transform(MultiTransformation(FitCenter(), RoundedCorners(10)))
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.user_photo_illustration)
                    .transform(MultiTransformation(FitCenter(), RoundedCorners(10)))
                    .error(R.drawable.user_photo_illustration))
            .into(imgView)
    }
}

@BindingAdapter("images")
fun bindRecyclerViewWithImages(recyclerView: RecyclerView, images: List<String>?) {
    images?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is DiaryGalleryAdapter -> {
                    submitImages(it)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@BindingAdapter("timeToDisplayFormat")
fun bindDisplayFormatTime(textView: TextView, time: Long?) {
    textView.text = time?.toDisplayFormat()
}

@SuppressLint("SetTextI18n")
@RequiresApi(Build.VERSION_CODES.N)
@BindingAdapter("displayPrice")
fun bindDisplayPrice(textView: TextView, price: String?) {
    textView.text = "$ $price"
}

@SuppressLint("SetTextI18n")
@RequiresApi(Build.VERSION_CODES.N)
@BindingAdapter("displayDollar")
fun bindDisplayDollar(textView: TextView, price: String?) {
    textView.text = "$price 元"
}

@SuppressLint("SetTextI18n")
@RequiresApi(Build.VERSION_CODES.N)
@BindingAdapter("displayCount")
fun bindDisplayCount(textView: TextView, count: String?) {
    textView.text = "$count 次"
}

@RequiresApi(Build.VERSION_CODES.N)
fun Long.toDisplayFormat(): String {
    return SimpleDateFormat("yyyy.MM.dd HH:mm").format(this)
}

@RequiresApi(Build.VERSION_CODES.N)
@BindingAdapter("dateToDisplayFormat")
fun bindDisplayFormatDate(textView: TextView, time: Long?) {
    textView.text = time?.toDisplayDateFormat()
}

@RequiresApi(Build.VERSION_CODES.N)
fun Long.toDisplayDateFormat(): String {
    return SimpleDateFormat("yyyy.MM.dd").format(this)
}

@BindingAdapter("setupApiStatus")
fun bindApiStatus(view: ProgressBar, status: LoadApiStatus?) {
    when (status) {
        LoadApiStatus.LOADING -> view.visibility = View.VISIBLE
        LoadApiStatus.DONE, LoadApiStatus.ERROR -> view.visibility = View.GONE
    }
}

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