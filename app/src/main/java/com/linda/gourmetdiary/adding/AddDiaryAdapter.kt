package com.linda.gourmetdiary.adding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.databinding.ItemAddingGalleryBinding

class AddDiaryAdapter(): ListAdapter<String, AddDiaryAdapter.AddDiaryViewHolder>(DiffCallback) {

    class AddDiaryViewHolder(private var binding: ItemAddingGalleryBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(store: String) {
            binding.viewModel = store
            //binding.imageGallery.setImageURI(store)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddDiaryViewHolder {
        return  AddDiaryViewHolder(ItemAddingGalleryBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: AddDiaryViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}