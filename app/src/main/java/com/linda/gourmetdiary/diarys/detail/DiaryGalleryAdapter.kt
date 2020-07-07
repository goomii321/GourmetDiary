package com.linda.gourmetdiary.diarys.detail

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.databinding.ItemDetailGalleryBinding
import com.linda.gourmetdiary.util.Logger.i
import java.util.logging.Logger

class DiaryGalleryAdapter: RecyclerView.Adapter<DiaryGalleryAdapter.ImageViewHolder>() {
    private lateinit var context: Context
    private var images: List<String>? = listOf("")

    class ImageViewHolder(private var binding: ItemDetailGalleryBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, imageUrl: String) {

            imageUrl.let {
                binding.image = it
                val displayMetrics = DisplayMetrics()
                (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
                binding.imageGallery.layoutParams = ConstraintLayout.LayoutParams(displayMetrics.widthPixels,
                    context.resources.getDimensionPixelSize(R.dimen.height_detail_gallery))

                binding.executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        context = parent.context
        return ImageViewHolder(ItemDetailGalleryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {

        images?.let {
            holder.bind(context, it[getRealPosition(position)])
        }
    }

    override fun getItemCount(): Int {
        return images?.let { Int.MAX_VALUE } ?: 1
    }

    private fun getRealPosition(position: Int): Int= images?.let {
            position % it.size
    } ?: 0

    fun submitImages(images: List<String>) {
        this.images = images
        notifyDataSetChanged()
    }
}