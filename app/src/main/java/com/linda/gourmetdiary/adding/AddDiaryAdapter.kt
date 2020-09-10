package com.linda.gourmetdiary.adding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linda.gourmetdiary.data.Diaries4Day
import com.linda.gourmetdiary.databinding.ItemAddImagesBinding
import com.linda.gourmetdiary.databinding.ItemAddingGalleryBinding
import com.linda.gourmetdiary.databinding.ItemDiaryCheckDayBinding
import com.linda.gourmetdiary.databinding.ItemDiarylistBinding
import com.linda.gourmetdiary.diaries.DiariesAdapter

class AddDiaryAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<AddDataItem, RecyclerView.ViewHolder>(DiffCallback) {

    class OnClickListener(val clickListener: (data: String) -> Unit) {
        fun onClick(data: String) = clickListener(data)
    }

    fun submitData(list: List<String>) {
        val items: List<AddDataItem> = when (list){
            emptyList<String >() -> listOf(AddDataItem.AddImages)
            else -> list.map { AddDataItem.Images(it) } + listOf(AddDataItem.AddImages)
        }
        submitList(items)
    }

    class AddViewHolder(private var binding: ItemAddImagesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(onClickListener: OnClickListener) {

            binding.root.setOnClickListener { onClickListener.onClick("") }

            binding.executePendingBindings()
        }
    }

    class AddDiaryViewHolder(private var binding: ItemAddingGalleryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(images: String) {
            binding.viewModel = images
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<AddDataItem>() {
        override fun areItemsTheSame(oldItem: AddDataItem, newItem: AddDataItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: AddDataItem, newItem: AddDataItem): Boolean {
            return oldItem == newItem
        }

        private const val ITEM_VIEW_IMAGES = 0
        private const val ITEM_VIEW_ADD_IMAGES = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)){
            is AddDataItem.AddImages -> ITEM_VIEW_ADD_IMAGES
            is AddDataItem.Images -> ITEM_VIEW_IMAGES
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_ADD_IMAGES -> AddViewHolder(
                ItemAddImagesBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            ITEM_VIEW_IMAGES -> AddDiaryViewHolder(
                ItemAddingGalleryBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddViewHolder -> {
                holder.bind(onClickListener)
            }
            is AddDiaryViewHolder -> {
                holder.bind((getItem(position) as AddDataItem.Images).image)
            }
        }
    }
}

sealed class AddDataItem {

    abstract val id: Long

    object AddImages : AddDataItem() {
        override val id = Long.MAX_VALUE
    }

    data class Images(val image: String) : AddDataItem() {
        override val id = 0L
    }
}