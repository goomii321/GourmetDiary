package com.linda.gourmetdiary.diaries.detail

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.databinding.ItemDetailGalleryBinding

class DiaryGalleryAdapter(private val onClickListener: OnClickListener): ListAdapter<String,DiaryGalleryAdapter.ImageViewHolder>(DiffCallback) {
    private lateinit var context: Context
    private lateinit var images: List<String>

    class OnClickListener(val clickListener: (string: String) -> Unit) {
        fun onClick(string: String) = clickListener(string)
    }

    class ImageViewHolder(private var binding: ItemDetailGalleryBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, imageUrl: String, onClickListener: OnClickListener) {
            binding.image = imageUrl
            val displayMetrics = DisplayMetrics()
            (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
            binding.imageGallery.layoutParams = ConstraintLayout.LayoutParams(displayMetrics.widthPixels,
                context.resources.getDimensionPixelSize(R.dimen.height_detail_gallery))
            binding.root.setOnClickListener { onClickListener.onClick(imageUrl) }
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        context = parent.context
        return ImageViewHolder(ItemDetailGalleryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {

        images?.let {
            holder.bind(context, it[getRealPosition(position)],onClickListener)
        }
    }

    override fun getItemCount(): Int {
        return images.let { Int.MAX_VALUE }
    }

    private fun getRealPosition(position: Int): Int= images.let {
            position % it.size
    }

    fun submitImages(images: List<String>) {
        this.images = images
        notifyDataSetChanged()
    }
}