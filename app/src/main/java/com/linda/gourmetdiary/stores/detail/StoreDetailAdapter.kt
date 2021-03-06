package com.linda.gourmetdiary.stores.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.databinding.ItemStoreOrderFoodBinding

class StoreDetailAdapter(private val onClickListener: OnClickListener):
    androidx.recyclerview.widget.ListAdapter<Diary, RecyclerView.ViewHolder>(DiffCallback) {

    class OnClickListener(val clickListener: (diary: Diary) -> Unit) {
        fun onClick(diary: Diary) = clickListener(diary)
    }

    class StoresViewHolder(private var binding: ItemStoreOrderFoodBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(diary: Diary, onClickListener: OnClickListener) {
            binding.viewModel = diary
            binding.root.setOnClickListener { onClickListener.onClick(diary) }
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Diary>() {
        override fun areItemsTheSame(oldItem: Diary, newItem: Diary): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: Diary, newItem: Diary): Boolean {
            return oldItem == newItem
        }

        private const val ITEM_VIEW_TYPE_STORES = 0x00
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_STORES -> StoresViewHolder(ItemStoreOrderFoodBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is StoresViewHolder -> {
                holder.bind((getItem(position) as Diary), onClickListener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return ITEM_VIEW_TYPE_STORES
    }
}