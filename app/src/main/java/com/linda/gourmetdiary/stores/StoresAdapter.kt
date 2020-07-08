package com.linda.gourmetdiary.stores

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.data.Store
import com.linda.gourmetdiary.data.Stores
import com.linda.gourmetdiary.data.Users
import com.linda.gourmetdiary.databinding.ItemDiarylistBinding
import com.linda.gourmetdiary.databinding.ItemStoresListBinding

class StoresAdapter(private val onClickListener: OnClickListener): ListAdapter<Stores, RecyclerView.ViewHolder>(DiffCallback) {

    class OnClickListener(val clickListener: (store: Stores) -> Unit) {
        fun onClick(store: Stores) = clickListener(store)
    }

    class StoresViewHolder(private var binding: ItemStoresListBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(store: Stores, onClickListener: OnClickListener) {

            binding.store = store
            binding.root.setOnClickListener { onClickListener.onClick(store) }
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Stores>() {
        override fun areItemsTheSame(oldItem: Stores, newItem: Stores): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: Stores, newItem: Stores): Boolean {
            return oldItem == newItem
        }

        private const val ITEM_VIEW_TYPE_STORES = 0x00
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_STORES -> StoresViewHolder(ItemStoresListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is StoresViewHolder -> {
                holder.bind((getItem(position) as Stores),onClickListener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return ITEM_VIEW_TYPE_STORES
    }
}